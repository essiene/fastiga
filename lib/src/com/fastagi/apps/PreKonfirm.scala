package com.fastagi.apps

import scala.actors.Actor
import scala.actors.Actor._
import com.fastagi.Session
import com.fastagi.util.PropertyFile

class PreKonfirm(session: Session) extends Actor with AgiTrait {
    
    val speechPath = PropertyFile.getProperty(prop, "agi.speech.out")

    override def rpc(request: AgiRequest): AgiResponse = {
        this.session ! request
        receive {
            case agiResponse: AgiResponse => 
                return agiResponse
            case _  => 
                return null                
        }
    }

    def getData(fileName: String, func: String => Unit) = {
      rpc(AgiGetData(speechPath + fileName, "", "")) match {
        case AgiResponse("-1", data, endpoint) =>
            quit("input-error")
        case AgiResponse(result, data, endpoint) =>
            func(result)
      }
    }

    def quit(messageFile: String): Unit = {
      rpc(AgiStreamFile(speechPath + messageFile, "\"\"", ""))
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
        rpc(AgiStreamFile(speechPath + "hello-prekonfirm", "\"\"", "")) match {        
            case AgiResponse("-1", data, endpos) =>
                quit("input-error")
            case AgiResponse("0", data, endpos) =>
                getAccountNumber()
        }
    }

    def getAccountNumber() = {
        getData("enter-account-number",
            (accountID) =>
                getAccountPin(accountID)
        )
    }

    def getAccountPin(accountID: String) = {
        getData("enter-pin",
            (accountPin) =>
                webService.isValid(accountID, accountPin) match {
                    case false =>
                        quit("auth-fail")
                    case true =>
                        getChequeNumber(accountID)
                }
        )
    }

    def getChequeNumber(accountID: String) = {
        getData("enter-cheque-number",
            (chequeNumber) =>
                getAmount(accountID, chequeNumber)
        )
    }

    def getAmount(accountID: String, chequeNumber: String) = {
        getData("enter-amount",
            (amount) =>
                getConfirmationStatus(accountID, chequeNumber, amount: String)
        )
    }

    def getConfirmationStatus(accountID: String, chequeNumber: String, amount: String) = {
       getData("confirm-options",
            (confirmationStatus) =>
                setConfirmationStatus(accountID, chequeNumber, confirmationStatus, amount)
       )
    }

    def setConfirmationStatus(accountID: String, chequeNumber: String, confirmationStatus: String, amount: String) = {
        
        confirmationStatus match {
            case "1" =>
            case "2" =>
            case "3" =>
            case "4" =>
            case _ =>
                quit("input-error")
        }
        if(webService.setConfirmationStatus(accountID, chequeNumber, confirmationStatus, amount)) {
            playGoodBye(confirmationStatus)
        }
        else {
            quit("input-error")
        }
    }

    def playGoodBye(confirmationStatus: String) {
        confirmationStatus match {
            case "1" =>
                quit("confirm")
            case "2" =>
                quit("cancel")
            case "3" => 
                quit("confirm-contact")
            case "4" =>
                quit("cancel-contact")
        }
    }

}
