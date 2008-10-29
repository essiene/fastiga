package com.fastagi.apps

import scala.actors.Actor
import scala.actors.Actor._
import com.fastagi.Session
import com.fastagi.AgiTrait

class Konfirm(session: Session) extends Actor with AgiTrait {
    
    var accountNumber = ""
    var userPin = ""
    var callid = ""
    var tries = 0

    def act() {
        this.start("hello")
    }


    def start(fileName: String): Unit = {
        this.rpc(AgiStreamFile(fileName, "\"\"", "")) match {        
            case AgiResponse(result, data, endpoint) =>
                this.accountNumber = this.agiUtils.getChannelVariable("extra", this)
                this.callid = this.agiUtils.getChannelVariable("callid", this)

                if(this.accountNumber == null || this.callid == null) {
                    session ! CloseSession
                    this.exit
                }

                this.userPin = this.agiUtils.getData("enter-pin", this)

                if(this.userPin.equals("-1")) {
                    session ! CloseSession
                    this.exit
                }

                if(this.agiUtils.validate(this.accountNumber, this.userPin, this.urlMaker, this.jsonPipe)) {
                    this.playCachedFile(this.callid)
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

    def playCachedFile(callid: String) = {
        this.agiUtils.playFile(callid, this)
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
        val url = urlMaker.url_for("customer", "konfirm", this.accountNumber, Map("status"->status, "callid"->this.callid, "pin"->this.userPin))

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
