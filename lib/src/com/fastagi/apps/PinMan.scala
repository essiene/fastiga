package com.fastagi.apps

import scala.actors.Actor
import scala.actors.Actor._
import com.fastagi.Session

class PinMan(session: Session) extends Actor {
    
    var agiRequests = List[AgiRequest]()
    
    def act() {
        this.playFile("hello")

        val account_number = this.getData("account-number")
        val pin = this.getData("enter-pin")
        val validated = this.validate(account_number, pin)

        if(validated) {
            val pin = this.getData("enter-new-pin")
            val pin2 = this.getData("enter-pin-again")
            if(matched(pin, pin2)) {
                this.updateDB(pin)
                session ! CloseSession
            }
        }

    }

    def playFile(fileName: String): String = {
        rpc(AgiStreamFile(fileName, "", ""))
        react {
            case AgiResponse(result, data, endpoint) =>
                return result
            case _ =>
                this.exit()
        }
    }

    def getData(fileName: String): String = {
        rpc(AgiGetData(fileName, "", "4"))
        react {
            case AgiResponse(result, data, endpoint) =>
                return result
            case _ =>
                this.exit()
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
