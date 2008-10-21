package com.fastagi.apps

import scala.actors.Actor
import scala.actors.Actor._
import com.fastagi.Session

class PinMan(session: Session) extends Actor {
    
    var agiRequests = List[AgiRequest]()
    var accountNumber = ""
    var pin = ""
    var newPin = ""
    var newPin2 = ""
    
    def act() {
        this.playHello("hello")
    }

    def playHello(fileName: String) = {
        rpc(AgiStreamFile(fileName, "", ""))
        receive {
            case AgiResponse(result, data, endpoint) =>
                println("hello")                
                this.getAccountNumber("enter-account-number")
        }                
    }

    def getAccountNumber(fileName: String) = {
        rpc(AgiGetData(fileName, "", "4"))
        receive {
            case AgiResponse(result, data, endpoint) =>                
                println(result)
                this.accountNumber = result
                this.getPin("enter-pin")
        }        
    }

    def getPin(fileName: String) = {
        rpc(AgiGetData(fileName, "", "4"))
        receive {
            case AgiResponse(result, data, endpoint) =>                
                println(result)
                this.pin = result
                if(this.validate(this.accountNumber, this.pin))
                    this.getNewPin("enter-pin")
        }
    }

    def getNewPin(fileName: String) = {
        rpc(AgiGetData(fileName, "", "4"))
        receive {
            case AgiResponse(result, data, endpoint) =>                
                println(result)
                this.newPin = result
                this.getNewPinAgain("enter-new-pin-again")
        }       
    }

    def getNewPinAgain(fileName: String) = {
        rpc(AgiGetData(fileName, "", "4"))
        receive {
            case AgiResponse(result, data, endpoint) =>                
                println(result)
                this.newPin2 = result
                if(this.matched(this.newPin, this.newPin2)) 
                    this.updateDB(this.newPin)
        }       
    }

    def validate(account_number: String, pin: String): boolean = {
        return true
    }

    def matched(pin: String, pin2: String): boolean = {
        return true
    }

    def updateDB(pin: String): boolean = {
        return true
    }
  
    def rpc(request: AgiRequest) = {
        this.session ! request
    }
}
