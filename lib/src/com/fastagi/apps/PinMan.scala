package com.fastagi.apps

import scala.actors.Actor
import scala.actors.Actor._

import com.fastagi.Session
import com.fastagi.AgiTrait
import com.fastagi.util.PropertyFile
import com.konfirmagi.webservice.WebService
import com.fastagi.apps.common.Common

class PinMan(session: Session) extends Actor with AgiTrait {
    
    val prop = PropertyFile.loadProperties("/etc/fastagi/agi.properties")
    val speechPath = PropertyFile.getProperty(prop, "agi.speech.out", "/etc/fastagi/speech/out/")
    val common = new Common(this, session, speechPath)


    def act() {
        this.begin()
    }

    def begin() = {
        remoteCall(session, AgiStreamFile(speechPath + "hello-pinman", "", "")) match {        
            case AgiResponse("-1", data, endpos) =>
                common.quit("input-error")
            case AgiResponse("0", data, endpos) =>
                getAccountNumber()
        }
        this
    }

    def getAccountNumber() = {
      common.getData("enter-account-number", 
        (accountNumber) =>
            getCurrentPin(accountNumber)
      )
    }

    def getCurrentPin(accountNumber: String) = {
        val webService = new WebService()
        common.getData("enter-current-pin",
            (currentPin) =>
              webService.isValid(accountNumber, currentPin) match {
                case false =>
                  common.quit("auth-fail")
                case true =>
                  getNewPin(accountNumber)
            }
        )
     }

    def getNewPin(accountNumber: String) = {
        common.getData("enter-new-pin",
            (newPin) =>
                getConfirmationPin(accountNumber, newPin)
        )
    }

    def getConfirmationPin(accountNumber: String, newPin: String) = {
        val webService = new WebService()
        common.getData("enter-new-pin-again",
            (confirmationPin) =>            
                newPin == confirmationPin match {
                  case false =>
                    common.quit("password-not-same")
                  case true =>
                    newPin.length() == 4 match {
                        case true =>
                            if(webService.changePin(accountNumber, newPin))
                                common.quit("pin-change-success")
                            else
                                common.quit("pin-change-fail")
                        case false =>
                            common.quit("password-length-mismatch")
                    }
                }
       )
     }
}
