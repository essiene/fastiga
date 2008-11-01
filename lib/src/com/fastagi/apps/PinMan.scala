package com.fastagi.apps

import scala.actors.Actor
import scala.actors.Actor._

import com.fastagi.Session
import com.fastagi.AgiTrait
import com.fastagi.util.PropertyFile
import com.konfirmagi.webservice.WebService

class PinMan(session: Session) extends Actor with AgiTrait {
    
    val prop = PropertyFile.loadProperties("/etc/fastagi/agi.properties")
    val speechPath = PropertyFile.getProperty(prop, "agi.speech.out")


    def getData(fileName: String, func: String => Unit) = {
      remoteCall(session, AgiGetData(speechPath + fileName, "", "")) match {
        case AgiResponse("-1", data, endpos) =>
            quit("input-error")
        case AgiResponse(result, data, endpos) =>
            func(result)
      }
    }

    def quit(messageFile: String): Unit = {
      remoteCall(session, AgiStreamFile(speechPath + messageFile, "\"\"", ""))
      quit()
    }

    def quit(): Unit = {
      session ! CloseSession
      this.exit()
    }

    def act() {
        this.begin()
    }

    def begin() = {
        remoteCall(session, AgiStreamFile(speechPath + "hello-pinman", "\"\"", "")) match {        
            case AgiResponse("-1", data, endpos) =>
                quit("input-error")
            case AgiResponse("0", data, endpos) =>
                getAccountNumber()
        }
        this
    }

    def getAccountNumber() = {
      getData("enter-account-number", 
        (accountNumber) =>
            getCurrentPin(accountNumber)
      )
    }

    def getCurrentPin(accountNumber: String) = {
        val webService = new WebService()
        getData("enter-current-pin",
            (currentPin) =>
              webService.isValid(accountNumber, currentPin) match {
                case false =>
                  quit("auth-fail")
                case true =>
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
        val webService = new WebService()
        getData("enter-new-pin-again",
            (confirmationPin) =>            
                newPin == confirmationPin match {
                  case false =>
                    quit("password-not-same")
                  case true =>
                    newPin.length() == 4 match {
                        case true =>
                            if(webService.changePin(accountNumber, newPin))
                                quit("pin-change-success")
                            else
                                quit("pin-change-fail")
                        case false =>
                            quit("password-too-short")
                    }
                }
       )
     }
}
