package com.fastagi.apps

import scala.actors.Actor
import scala.actors.Actor._
import com.fastagi.Session
import com.fastagi.AgiTrait

class PinMan(session: Session) extends Actor with AgiTrait {
    
    var accountNumber = ""
    var oldPin = ""
    var newPin = ""
    var newPin2 = ""
    
    def act() {
        this.start("hello")
    }

    def start(fileName: String) = {
        rpc(AgiStreamFile(fileName, "\"\"", "")) match {        
            case AgiResponse(result, data, endpoint) =>
                this.accountNumber = this.agiUtils.getData("enter-account-number", this)

                if(this.accountNumber.equals("-1")) {
                    session ! CloseSession
                    this.exit
                }

                if(this.agiUtils.validate(this.accountNumber, this.urlMaker, this.jsonPipe)) {

                    this.oldPin = this.agiUtils.getData("enter-pin", this)
                    this.newPin = this.agiUtils.getData("new-pin", this)
                    this.newPin2 = this.agiUtils.getData("new-pin-again", this)
                    
                    if(this.matched(this.newPin, this.newPin2))                     
                        this.updateDB()
                    else {
                        rpc(AgiStreamFile("pin-mismatch", "\"\"", ""))
                        session ! CloseSession
                        this.exit
                    }
                } else {
                    rpc(AgiStreamFile("invalid-account-number", "\"\"", ""))
                    session ! CloseSession
                    this.exit
                }
        }                
    }

    def matched(pin: String, pin2: String): boolean = {
        if(pin.equals(pin2))
            return true
        else
            return false
    }

    def updateDB(): boolean = {        
        val url = urlMaker.url_for("customer", "changepin", this.accountNumber, Map("oldpin"->this.oldPin, "newpin"->this.newPin))

        jsonPipe.parse(url)

        val status = jsonPipe.get("Status")
        session ! CloseSession
        if(status.equals("OK"))
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
