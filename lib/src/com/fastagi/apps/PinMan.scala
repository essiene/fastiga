package com.fastagi.apps

import scala.actors.Actor
import scala.actors.Actor._
import com.fastagi.Session
import com.fastagi.AgiTrait
import com.fastagi.util.AgiUtils
import com.fastagi.apps.util.JSONPipe

class PinMan(session: Session) extends Actor with AgiTrait {
    
    var accountNumber = ""
    var pin = ""
    var newPin = ""
    var newPin2 = ""
    //val validate_url = config.get("validate-url")
    val validate_url = "http://localhost:5000/ivr/validateuser?"
    val changepin_url = "http://localhost:5000/pinman/changepin?"

    
    def act() {
        this.start("hello")
    }

    def start(fileName: String) = {
        rpc(AgiStreamFile(fileName, "", "")) match {        
            case AgiResponse(result, data, endpoint) =>
                this.accountNumber = AgiUtils.getData("enter-account-number", this)
                this.pin = AgiUtils.getData("enter-pin", this)

                if(this.validate(this.accountNumber, this.pin)) {
                    this.newPin = AgiUtils.getData("new-pin", this)
                    this.newPin2 = AgiUtils.getData("new-pin-again", this)
                    
                    if(this.matched(this.newPin, this.newPin2)) 
                        this.updateDB(this.accountNumber, this.newPin)
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

    def validate(account_number: String, pin: String): boolean = {
        JSONPipe.parse(validate_url + "account_number="+account_number+"&pin="+pin)

        val status = JSONPipe.get("Status")
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

    def updateDB(account_number: String, pin: String): boolean = {
        JSONPipe.parse(changepin_url + "account_number="+account_number+"&pin="+pin)

        val status = JSONPipe.get("Status")
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
