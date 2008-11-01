package com.fastagi.apps

import scala.actors.Actor
import scala.actors.Actor._
import com.fastagi.Session
import com.fastagi.AgiTrait
import com.fastagi.util.PropertyFile

class Konfirm(session: Session) extends Actor with AgiTrait {
    
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

    def begin() {
       rpc(AgiStreamFile(speechPath + "hello-konfirm", "\"\"", "")) match {
           case AgiResponse("-1", data, endpos) =>
               quit("input-error")
           case AgiResponse("0", data, endpos) =>
                getAccountNumber
       }
    }

    def getAccountNumber() = {
        rpc(AgiGetChannelVariable("accountid")) match {
            case AgiResponse("0", data, endpos) =>
                quit("input-error")
            case AgiResponse("1", accountID, endpos) =>
                getTransactionID(accountID)
        }
    }

    def getTransactionID(accountID: String) = {
        rpc(AgiGetChannelVariable("transactionid")) match {
            case AgiResponse("0", data, endpos) =>
                quit("input-error")
            case AgiResponse("1", transactionID, endpos) =>
                getChequeNumber(accountID, transactionID)
        }
    }

    def getChequeNumber(accountID: String, transactionID: String) = {
        rpc(AgiGetChannelVariable("chequenumber")) match {
            case AgiResponse("0", data, endpos) =>
                quit("input-error")
            case AgiResponse("1", chequeNumber, endpos) =>
                getAccountPin(accountID, transactionID, chequeNumber)
        }
    }
    
    def getAccountPin(accountID: String, transactionID: String, chequeNumber: String) = {
        getData("enter-pin", 
            (accountPin) =>
                webService.isValid(accountID, accountPin) match {
                    case false =>
                        quit("auth-fail")
                    case true =>
                        playCachedFile(accountID, transactionID, chequeNumber)
                }
        )
    }

    def playCachedFile(accountID: String, transactionID: String, chequeNumber: String) = {
        val cachePath = PropertyFile.getProperty(prop, "agi.speech.cache")
        //TODO: use path.join equivalent
        rpc(AgiStreamFile(cachePath + transactionID, "\"\"", "")) match {
            case AgiResponse("-1", data, endpos) =>
                quit("input-error")
            case AgiResponse("0", data, endpos) =>
                getConfirmationStatus(accountID, transactionID, chequeNumber)
        }
    }

    def getConfirmationStatus(accountID: String, transactionID: String, chequeNumber: String) = {
        getData("confirm-options", 
            (confirmationStatus) =>                
                setConfirmationStatus(accountID, transactionID, chequeNumber, confirmationStatus)
        )
    }

    def setConfirmationStatus(accountID: String, transactionID: String, chequeNumber: String, confirmationStatus: String) = {

        confirmationStatus match {
            case "1" =>
            case "2" =>
            case "3" =>
            case "4" =>
            case _ =>
                quit("input-error")
        }

        webService.setConfirmationStatus(accountID, chequeNumber, confirmationStatus, transactionID) match {
            case true => 
                playGoodBye(confirmationStatus)
            case false =>
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
