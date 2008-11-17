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
    val speechPath = PropertyFile.getProperty(prop, "agi.speech.out", "/etc/fastagi/speech/out/")
    val common = new Common(this, session, speechPath)
    
    def act() {
        this.begin()
    }

    def begin() {
        getTransactionID()
    }    

    def getTransactionID() = {
        remoteCall(session, AgiGetChannelVariable("callid")) match {
            case AgiResponse("0", data, endpos) =>
                common.quit("input-error")
            case AgiResponse("1", transactionID, endpos) =>
                getExtras(transactionID)
        }
    }

    def getExtras(transactionID: String) {
        remoteCall(session, AgiGetChannelVariable("extra")) match {
            case AgiResponse("0", data, endpos) =>
                common.quit("input-error")
            case AgiResponse("1", extra, endpos) =>
                val extraList = List fromString(extra,'+')
                println(extraList)
                getAccountNumber(transactionID, extraList)
        }
    }

    def getAccountNumber(transactionID: String, extraList: List[String]) = {
        println(extraList(0))
        getChequeNumber(transactionID, extraList(0), extraList)
    }

    def getChequeNumber(transactionID: String, accountID: String, extraList: List[String]) = {        
        println(extraList(1))
        playHello(transactionID, accountID, extraList(1))
    }

    def playHello(transactionID: String, accountID: String, chequeNumber: String) = {
        val file = new File(speechPath, "hello-konfirm")

        remoteCall(session, AgiStreamFile(file.getAbsolutePath(), "", "")) match {
            case AgiResponse("-1", data, endpos) =>
                setConfirmationStatus(transactionID, accountID, chequeNumber, "0")
            case AgiResponse("0", data, endpos) =>
                getAccountPin(transactionID, accountID, chequeNumber)
        }
    }
    
    def getAccountPin(transactionID: String, accountID: String, chequeNumber: String) = {
        val webService = new WebService()
        common.getData("enter-pin", 
            (accountPin) =>
                webService.isValid(accountID, accountPin) match {
                    case false =>
                        playAuthFail(transactionID, accountPin, chequeNumber)
                    case true =>
                        playCachedFile(transactionID, accountID, chequeNumber)
                }
        )
    }

    def playAuthFail(transactionID: String, accountID: String, chequeNumber: String) = {
        val file = new File(speechPath, "auth-fail")
        remoteCall(session, AgiStreamFile(file.getAbsolutePath(), "", "")) 
        setConfirmationStatus(transactionID, accountID, chequeNumber, "0")
    }

    def playCachedFile(transactionID: String, accountID: String, chequeNumber: String) = {
        val cachePath = PropertyFile.getProperty(prop, "agi.speech.cache", "/etc/fastagi/cache/")
        val file = new File(cachePath, transactionID)
        remoteCall(session, AgiStreamFile(file.getAbsolutePath(), "\"\"", "")) match {
            case AgiResponse("-1", data, endpos) =>
                setConfirmationStatus(transactionID, accountID, chequeNumber, "0")
            case AgiResponse("0", data, endpos) =>
                getConfirmationStatus(transactionID, accountID, chequeNumber)
        }
    }

    def getConfirmationStatus(transactionID: String, accountID: String, chequeNumber: String) = {
        common.getData("confirm-options", 
            (confirmationStatus) =>                
                setConfirmationStatus(transactionID, accountID, chequeNumber, confirmationStatus)
        )
    }

    def setConfirmationStatus(transactionID: String, accountID: String, chequeNumber: String, confirmationStatus: String): Unit = {
        val webService = new WebService()

        confirmationStatus match {
            case "1" =>
            case "2" =>
            case "3" =>
            case "4" =>
            case "0" =>
            case _ =>            
                setConfirmationStatus(transactionID, accountID, chequeNumber, "0")
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
            case "0" =>
                
        }
    }
}
