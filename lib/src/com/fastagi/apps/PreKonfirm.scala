package com.fastagi.apps

import scala.actors.Actor
import scala.actors.Actor._
import com.fastagi.Session
import com.fastagi.util.PropertyFile
import com.konfirmagi.webservice.WebService
import com.fastagi.apps.common.Common

class PreKonfirm(session: Session) extends Actor with AgiTrait {
    
    val prop = PropertyFile.loadProperties("/etc/fastagi/agi.properties")
    val speechPath = PropertyFile.getProperty(prop, "agi.speech.out", "/etc/fastagi/speech/out/")
    val common = new Common(this, session, speechPath)

    def act() {
        this.begin()
    }

    def begin() = {
        remoteCall(session, AgiStreamFile(speechPath + "hello-prekonfirm", "\"\"", "")) match {        
            case AgiResponse("-1", data, endpos) =>
                common.quit("input-error")
            case AgiResponse("0", data, endpos) =>
                getAccountNumber()
        }
    }

    def getAccountNumber() = {
        common.getData("enter-account-number",
            (accountID) =>
                getAccountPin(accountID)
        )
    }

    def getAccountPin(accountID: String) = {
        val webService = new WebService()
        common.getData("enter-pin",
            (accountPin) =>
                webService.isValid(accountID, accountPin) match {
                    case false =>
                        common.quit("auth-fail")
                    case true =>
                        getChequeNumber(accountID)
                }
        )
    }

    def getChequeNumber(accountID: String) = {
        common.getData("enter-cheque-number",
            (chequeNumber) =>
                getAmount(accountID, chequeNumber)
        )
    }

    def getAmount(accountID: String, chequeNumber: String) = {
        common.getData("enter-amount",
            (amount) =>
                getConfirmationStatus(accountID, chequeNumber, amount: String)
        )
    }

    def getConfirmationStatus(accountID: String, chequeNumber: String, amount: String) = {
       common.getData("confirm-options",
            (confirmationStatus) =>
                setConfirmationStatus(accountID, chequeNumber, confirmationStatus, amount)
       )
    }

    def setConfirmationStatus(accountID: String, chequeNumber: String, confirmationStatus: String, amount: String) = {
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

        webService.setPreConfirmationStatus(accountID, chequeNumber, confirmationStatus, amount) match {
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
