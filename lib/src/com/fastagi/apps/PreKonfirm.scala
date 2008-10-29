package com.fastagi.apps

import scala.actors.Actor
import scala.actors.Actor._
import com.fastagi.Session

class PreKonfirm(session: Session) extends Actor with AgiTrait {
    
    var accountNumber = ""
    var pin = ""
    var chequeNumber = ""
    
    def act() {
        this.start("hello")
    }

    def start(fileName: String) = {
        rpc(AgiStreamFile(fileName, "", "")) match {        
            case AgiResponse(result, data, endpoint) =>
                this.accountNumber = this.agiUtils.getData("enter-account-number", this)
                if(this.agiUtils.validate(this.accountNumber, this.urlMaker, this.jsonPipe)) {
                    this.pin = this.agiUtils.getData("enter-pin", this)
                    this.chequeNumber = this.agiUtils.getData("enter-cheque-number", this)
                    this.getConfirmationStatus("play-options")
                } else {
                    rpc(AgiStreamFile("invalid-account-number", "", ""))
                    session ! CloseSession
                }
        }                
    }

    def getConfirmationStatus(fileName: String) = {
        var status = ""
        rpc(AgiGetData(fileName,  "", "")) match {
            case AgiResponse(result, data, endpoint) =>
                Integer.parseInt(result) match {
                    case 1 => 
                        //konfirm
                        status = result
                    case 2 =>
                        //cancel
                        status = result
                    case 3 =>
                        //konfirm and call
                        status = result
                    case 4 =>
                        //cancel and call
                        status = result
                    case _ =>
                        session ! CloseSession
                }
                updateDB(status)
        }
    }

    def updateDB(status: String): boolean = {    
        val url = urlMaker.url_for("user", "prekonfirm", this.accountNumber, Map("status"->status, "chequenumber"->this.chequeNumber, "pin"->this.pin))

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
            case agiRequest: AgiResponse =>
                return agiRequest
        }
    }
}
