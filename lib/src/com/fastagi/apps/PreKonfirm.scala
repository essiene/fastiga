package com.fastagi.apps

import scala.actors.Actor
import scala.actors.Actor._
import com.fastagi.Session
import com.fastagi.util.PropertyFile
import com.konfirmagi.webservice.WebService
import com.fastagi.apps.common.Common

class PreKonfirm(session: Session) extends Actor with AgiTrait {
    
    val common = new Common(this, session, "")
    val errorFile = common.getFullPath("input-error", "recorded")

    def act() {
        this.begin()
    }

    def begin() = {
        val filePath = common.getFullPath("hello-prekonfirm", "recorded")

        filePath match {
            case "" =>
                common.quit(errorFile)
            case file =>
                remoteCall(session, AgiStreamFile(file, "", "")) match {        
                    case AgiResponse("-1", data, endpos) =>
                        common.quit(file)
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
                    (accountID) =>
                        getAccountPin(accountID)
                )
        }
    }

    def getAccountPin(accountID: String) = {
        val webService = new WebService()

        val filePath = common.getFullPath("enter-pin", "recorded")

        filePath match {
            case "" =>
                common.quit(errorFile)
            case file =>
                common.getData(file, errorFile, 
                    (accountPin) =>
                        webService.isValid(accountID, accountPin) match {
                            case false =>
                                common.quit("auth-fail")
                            case true =>
                                getChequeNumber(accountID)
                        }
                )
        }
    }

    def getChequeNumber(accountID: String) = {
        val filePath = common.getFullPath("enter-cheque-number", "recorded")

        filePath match {
            case "" =>
                common.quit(errorFile)
            case file =>
                common.getData(file, errorFile, 
                    (chequeNumber) =>
                        getAmount(accountID, chequeNumber)
                )
        }
    }

    def getAmount(accountID: String, chequeNumber: String) = {
        val filePath = common.getFullPath("enter-amount", "recorded")

        filePath match {
            case "" =>
                common.quit(errorFile)
            case file =>
                common.getData(file, errorFile, 
                    (amount) =>
                        getConfirmationStatus(accountID, chequeNumber, amount: String)
                )
        }
    }

    def getConfirmationStatus(accountID: String, chequeNumber: String, amount: String) = {
        val filePath = common.getFullPath("confirm-action", "recorded")

        filePath match {
            case "" =>
                common.quit(errorFile)
            case file =>
                common.getData(file, errorFile, 
                    (confirmationStatus) =>
                        setConfirmationStatus(accountID, chequeNumber, confirmationStatus, amount)
                )
        }
    }

    def setConfirmationStatus(accountID: String, chequeNumber: String, confirmationStatus: String, amount: String) = {
        val webService = new WebService()
        
        confirmationStatus match {
            case "1" =>
            case "2" =>
            case "3" =>
            case "4" =>
            case "0" => 
            case _ =>
                webService.setPreConfirmationStatus(accountID, chequeNumber, "0", amount)
                common.quit(errorFile)
                this.exit()
        }

        webService.setPreConfirmationStatus(accountID, chequeNumber, confirmationStatus, amount) match {
            case true =>
                playGoodBye(confirmationStatus)
            case false =>
                common.quit(errorFile)
        }
    }

    def playGoodBye(confirmationStatus: String) {
        val file = this.getConfirmPath(confirmationStatus, "recorded")
        common.quit(file)
    }

    def getConfirmPath(confirmationStatus: String, path: String): String = {
        val webService = new WebService()
        webService.getConfirmPath(confirmationStatus, "konfirm", path) match {
            case "" =>
                return ""
            case confirmPath =>
                return confirmPath
        }
    }
}
