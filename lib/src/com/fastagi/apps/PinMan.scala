package com.fastagi.apps

import scala.actors.Actor
import scala.actors.Actor._
import com.fastagi.Session
import com.fastagi.AgiTrait
import com.fastagi.util.AgiUtils

class PinMan(session: Session) extends Actor with AgiTrait {
    
    var accountNumber = ""
    var pin = ""
    var newPin = ""
    var newPin2 = ""
    
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
                    
                    if(this.matched(this.newPin, this.newPin2)) {
                        this.updateDB(this.newPin)
                    }

                }
        }                
    }

    def validate(account_number: String, pin: String): boolean = {
        return true
    }

    def matched(pin: String, pin2: String): boolean = {
        return true
    }

    def updateDB(pin: String): boolean = {
        session ! CloseSession
        this.exit()
        return true
    }
  
    override def rpc(request: AgiRequest): AgiResponse = {
        this.session ! request
        receive {
            case agiRequest: AgiResponse =>
                return agiRequest
        }
    }
}
