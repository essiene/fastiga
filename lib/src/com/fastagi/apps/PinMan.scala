package com.fastagi.apps

import scala.actors.Actor
import scala.actors.Actor._

import com.fastagi.Session
import com.fastagi.AgiTrait
import com.konfirmagi.webservice.WebService
import com.fastagi.apps.common.Common

class PinMan(session: Session) extends Actor with AgiTrait {
    
    val common = new Common(this, session, "")
    val errorFile = common.getFullPath("input-error", "recorded")

    def act() {
        this.begin()
    }

    def begin() = {
        val filePath = common.getFullPath("hello-pinman", "recorded")
        filePath match {
            case "" =>
                common.quit(errorFile)
            case file =>                
                remoteCall(session, AgiStreamFile(file, "", "")) match {        
                    case AgiResponse("-1", data, endpos) =>
                        common.quit(errorFile)
                    case AgiResponse("0", data, endpos) =>
                        getAccountNumber()
                }
        }
    }

    def getAccountNumber() = {      
        val filePath = common.getFullPath("enter-account-number", "recorded")
        filePath match {
            case "" =>
                common.quit(errorFile)
            case file =>                
                common.getData(file, errorFile,  
                    (accountNumber) =>
                        getCurrentPin(accountNumber)
                )
        }                
    }

    def getCurrentPin(accountNumber: String) = {
        val webService = new WebService()

        val filePath = common.getFullPath("enter-current-pin", "recorded")

        val authFailPath = common.getFullPath("auth-fail", "recorded")

        filePath match {
            case "" =>
                common.quit(errorFile)
            case file =>
                common.getData(file, errorFile, 
                    (currentPin) =>
                      webService.isValid(accountNumber, currentPin) match {
                        case false =>
                          common.quit(authFailPath)
                        case true =>
                          getNewPin(accountNumber)
                    }
                )
            }
     }

    def getNewPin(accountNumber: String) = {
        val filePath = common.getFullPath("enter-new-pin", "recorded")

        filePath match {
            case "" =>
                common.quit(errorFile)
            case file =>
                common.getData(file, errorFile, 
                    (newPin) =>
                        getConfirmationPin(accountNumber, newPin)
                )
        }
    }

    def getConfirmationPin(accountNumber: String, newPin: String) = {
        val webService = new WebService()

        val filePath = common.getFullPath("enter-new-pin-again", "recorded")
        val pinErrorPath = common.getFullPath("pin-not-same", "recorded")
        val successPath = common.getFullPath("pin-change-success", "recorded")
        val failPath = common.getFullPath("pin-change-fail", "recorded")
        val lenghtMismatchPath = common.getFullPath("pin-length-mismatch", "recorded")

        filePath match {
            case "" =>
                common.quit(errorFile)
            case file =>
                common.getData(file, errorFile,
                    (confirmationPin) =>            
                        newPin == confirmationPin match {
                          case false =>
                            common.quit(pinErrorPath)
                          case true =>
                            newPin.length() == 4 match {
                                case true =>
                                    if(webService.changePin(accountNumber, newPin))
                                        common.quit(successPath)
                                    else
                                        common.quit(failPath)
                                case false =>
                                    common.quit(lenghtMismatchPath)
                            }
                        }
               )
       }
     }
}
