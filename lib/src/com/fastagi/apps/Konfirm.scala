package com.fastagi.apps

import scala.actors.Actor
import scala.actors.Actor._
import com.fastagi.Session
import com.fastagi.AgiTrait

class Konfirm(session: Session) extends Actor with AgiTrait {
    
    var accountNumber = ""
    var chequeNumber = ""
    var userPin = ""
    var transactionID = ""
    var tries = 0

    def act() {
        this.start("hello")
    }


    def start(fileName: String): Unit = {
        this.rpc(AgiStreamFile(fileName, "\"\"", "")) match {        
            case AgiResponse(result, data, endpoint) =>
                this.accountNumber = this.agiUtils.getChannelVariable("accountid", this)
                this.transactionID = this.agiUtils.getChannelVariable("transactionID", this)
                this.chequeNumber = this.agiUtils.getChannelVariable("chequenumber", this)

                if(this.accountNumber == null || this.transactionID == null) {
                    session ! CloseSession
                    this.exit
                }

                this.userPin = this.agiUtils.getData("enter-pin", this)

                if(this.userPin.equals("-1")) {
                    session ! CloseSession
                    this.exit
                }

                if(this.agiUtils.validate(this.accountNumber, this.userPin, this.urlMaker, this.jsonPipe)) {
                    this.playCachedFile(this.transactionID)
                } else {
                    this.agiUtils.playFile("invalid-pin-entered", this)
                    if(tries < 3) {
                        tries = tries + 1
                        this.restart("hello")
                    }
                }

        }     
    }

    def restart(fileName: String) = this.start(fileName)

    def playCachedFile(transactionID: String) = {
        this.agiUtils.playFile(transactionID, this)
        this.getConfirmationStatus("play-options")
    }

    def getConfirmationStatus(fileName: String) = {
        var status = ""
        this.rpc(AgiGetData(fileName, "", "")) match {
            case AgiResponse(result, data, endpoint) =>
                Integer.parseInt(result) match {
                    case 1 => 
                        //konfirm
                        status = result
                    case 2 => 
                        //cancel
                        status = result
                    case 3 => 
                        //konfirm and contact
                        status = result
                    case 4 => 
                        //cancel and contact
                        status = result
                    case _ =>
                        session ! CloseSession
                }
                this.updateDB(status)
        }
    }

    def updateDB(status: String): boolean = {
        var url = urlMaker.url_for("account", "konfirm", this.accountNumber, 
                                    Map("status"->status, "transactionid"->this.transactionID, "pin"->this.userPin))
        
        var front = url.substring(0,url.indexOf("konfirm"))
        var back = url.substring(url.indexOf("konfirm"), url.length())
        front = front + "cheque/" + this.chequeNumber + "/"
        url = front + back

        jsonPipe.parse(url)

        val retVal = jsonPipe.get("Status")
        session ! CloseSession
        if(retVal.equals("OK"))
            return true
        return false            
    }    

    override def rpc(request: AgiRequest): AgiResponse = {
        this.session ! request
        receive {
            case agiResponse: AgiResponse =>
                return agiResponse
        }
    }
}
