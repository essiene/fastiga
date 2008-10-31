package com.fastagi.apps

import scala.actors.Actor
import scala.actors.Actor._
import com.fastagi.Session
import com.fastagi.AgiTrait

class PinMan(session: Session) extends Actor with AgiTrait {
    
    def act() {
        this.start()
    }

    def quit(messageFile: String) = {
      rpc(AgiStreamFile(messageFile, "\"\"", ""))
      quit()
    }

    def quit() = {
      session ! CloseSession
    }

    def getData(fileName: String, func: (result: String) => Unit) = {
      rpc(AgiGetData(fileName, "", "")) match {
        case AgiResponse(-1, data, endpoint) =>
            quit("input-error")
        case AgiResponse(result, data, endpoint) =>
            func(result)
      }
    }

    def start() = {
        rpc(AgiStreamFile("hello", "\"\"", "")) match {        
            case AgiResponse(-1, data, endpoint) =>
                quit("input-error")
            case AgiResponse(0, data, endpoint) =>
                getAccountNumber()
        }
    }

    def getAccountNumber() = {
      getData("enter-account-number", 
        (accountNumber) =>
            getCurrentPin(accountNumber)
      )
    }

    def getCurrentPin(accountNumber: String) = {
        getData("enter-current-pin",
            (currentPin) =>
              isValid(accountNumber, currentPin) match {
                case "FAIL" =>
                  quit("auth-fail")
                case "OK" =>
                  getNewPin(accountNumber)
            }
        )
     }

    def getNewPin(accountNumber: String) = {
        getData("enter-new-pin",
            (newPin) =>
                getConfirmationPin(accountNumber, newPin)
        )
    }

    def getConfirmationPin(accountNumber: String, newPin: String) = {
       getData("enter-new-pin-again",
         (confirmationPin) =>
            newPin == confirmationPin match {
              case false =>
                quit("password-not-same")
              case true =>
                changePassword(accountNumber, newPin)
            }
       )
     }


     def changePassword(accountNumber: String, newPin: String) = {
       //do app server stuff here
       quit("thank-you-for-banking-with-us")
     }

    def updateDB(): boolean = {        
        val url = urlMaker.url_for("account", "change_pin", this.accountNumber, Map("oldpin"->this.oldPin, "newpin"->this.newPin))

        jsonPipe.parse(url)

        val status = jsonPipe.get("Status")
        session ! CloseSession
        if(status.equals("OK"))
            return true
        return false
    }
}
