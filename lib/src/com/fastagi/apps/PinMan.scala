package com.fastagi.apps

import scala.actors.Actor
import scala.actors.Actor._
import com.fastagi.Session
import com.fastagi.AgiTrait
import com.fastagi.util.AgiUtils
import com.fastagi.apps.util._

class PinMan(session: Session) extends Actor with AgiTrait {
    
    var accountNumber = ""
    var oldPin = ""
    var newPin = ""
    var newPin2 = ""
    var jsonPipe = new JSONPipe()
    var urlMaker = new URLMaker()
    
    def act() {
        this.start("hello")
    }

    def start(fileName: String) = {
        rpc(AgiStreamFile(fileName, "", "")) match {        
            case AgiResponse(result, data, endpoint) =>
                this.accountNumber = AgiUtils.getData("enter-account-number", this)

                if(this.validate(this.accountNumber)) {

                    this.oldPin = AgiUtils.getData("enter-pin", this)
                    this.newPin = AgiUtils.getData("new-pin", this)
                    this.newPin2 = AgiUtils.getData("new-pin-again", this)
                    
                    if(this.matched(this.newPin, this.newPin2))                     
                        this.updateDB(this.accountNumber,  this.oldPin, this.newPin)
                    else {
                        rpc(AgiStreamFile("pin-mismatch", "", ""))
                        session ! CloseSession
                    }
                } else {
                    rpc(AgiStreamFile("invalid-account-number", "", ""))
                    session ! CloseSession
                }
        }                
    }

    def validate(account_number: String): boolean = {        
        val url = urlMaker.url_for("user", null, account_number, null)
        
        jsonPipe.parse(url)

        val status = jsonPipe.get("Status")
        if(status.equals("OK"))
            return true
        return false            
    }

    def matched(pin: String, pin2: String): boolean = {
        if(pin.equals(pin2))
            return true
        else
            return false
    }

    def updateDB(account_number: String, oldPin: String, newPin: String): boolean = {        
        val url = urlMaker.url_for("user", "changepin", account_number, Map("oldpin"->oldPin, "newpin"->newPin))

        jsonPipe.parse(url)

        val status = jsonPipe.get("Status")
        session ! CloseSession
        if(status.equals("OK"))
            return true
        else
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
