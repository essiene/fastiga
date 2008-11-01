package com.fastagi.apps

import java.io.File

import scala.actors.Actor
import scala.actors.Actor._
import com.fastagi.Session
import com.fastagi.AgiTrait
import com.fastagi.util.PropertyFile
import com.konfirmagi.webservice.WebService
import com.fastagi.apps.common.Common

class Konfirm(session: Session) extends Actor with AgiTrait {
    
    val prop = PropertyFile.loadProperties("/etc/fastagi/agi.properties")
    val speechPath = PropertyFile.getProperty(prop, "agi.speech.out")
    val common = new Common(this, session, speechPath)
    
    def act() {
        this.begin()
    }

    def begin() {
       val file = new File(speechPath, "hello-konfirm")
       remoteCall(session, AgiStreamFile(file.getAbsolutePath(), "\"\"", "")) match {
           case AgiResponse("-1", data, endpos) =>
               common.quit("input-error")
           case AgiResponse("0", data, endpos) =>
                getAccountNumber
       }
    }

    def getAccountNumber() = {
        remoteCall(session, AgiGetChannelVariable("accountid")) match {
            case AgiResponse("0", data, endpos) =>
                common.quit("input-error")
            case AgiResponse("1", accountID, endpos) =>
                getTransactionID(accountID)
        }
    }

    def getTransactionID(accountID: String) = {
        remoteCall(session, AgiGetChannelVariable("transactionid")) match {
            case AgiResponse("0", data, endpos) =>
                common.quit("input-error")
            case AgiResponse("1", transactionID, endpos) =>
                getChequeNumber(accountID, transactionID)
        }
    }

    def getChequeNumber(accountID: String, transactionID: String) = {
        remoteCall(session, AgiGetChannelVariable("chequenumber")) match {
            case AgiResponse("0", data, endpos) =>
                common.quit("input-error")
            case AgiResponse("1", chequeNumber, endpos) =>
                getAccountPin(accountID, transactionID, chequeNumber)
        }
    }
    
    def getAccountPin(accountID: String, transactionID: String, chequeNumber: String) = {
        val webService = new WebService()
        common.getData("enter-pin", 
            (accountPin) =>
                webService.isValid(accountID, accountPin) match {
                    case false =>
                        common.quit("auth-fail")
                    case true =>
                        playCachedFile(accountID, transactionID, chequeNumber)
                }
        )
    }

    def playCachedFile(accountID: String, transactionID: String, chequeNumber: String) = {
        val cachePath = PropertyFile.getProperty(prop, "agi.speech.cache")
        val file = new File(cachePath, transactionID)
        remoteCall(session, AgiStreamFile(file.getAbsolutePath(), "\"\"", "")) match {
            case AgiResponse("-1", data, endpos) =>
                common.quit("input-error")
            case AgiResponse("0", data, endpos) =>
                getConfirmationStatus(accountID, transactionID, chequeNumber)
        }
    }

    def getConfirmationStatus(accountID: String, transactionID: String, chequeNumber: String) = {
        common.getData("confirm-options", 
            (confirmationStatus) =>                
                setConfirmationStatus(accountID, transactionID, chequeNumber, confirmationStatus)
        )
    }

    def setConfirmationStatus(accountID: String, transactionID: String, chequeNumber: String, confirmationStatus: String) = {
        val webService = new WebService()

        confirmationStatus match {
            case "1" =>
            case "2" =>
            case "3" =>
            case "4" =>
            case _ =>
                common.quit("input-error")
                this.exit()
        }

        webService.setConfirmationStatus(accountID, chequeNumber, confirmationStatus, transactionID) match {
            case true => 
                playGoodBye(confirmationStatus)
            case false =>
                common.quit("input-error")
        }
    }

    def playGoodBye(confirmationStatus: String) {
        confirmationStatus match {
            case "1" =>
                common.quit("confirm")
            case "2" =>
                common.quit("cancel")
            case "3" => 
                common.quit("confirm-contact")
            case "4" =>
                common.quit("cancel-contact")
        }
    }
}
