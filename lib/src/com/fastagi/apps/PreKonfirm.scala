package com.fastagi.apps

import scala.actors.Actor
import scala.actors.Actor._
import com.fastagi.Session

class PreKonfirm(session: Session) extends Actor {
    
    var accountNumber = ""
    var pin = ""
    var chequeNumber = ""
    
    def act() {
        this.playHello("hello")
    }

    def playHello(fileName: String) = {
        rpc(AgiStreamFile(fileName, "", "")) match {        
            case AgiResponse(result, data, endpoint) =>
                this.getAccountNumber("enter-account-number")
        }                
    }

    def getAccountNumber(fileName: String) = {
        rpc(AgiGetData(fileName, "", "4")) match {
            case AgiResponse(result, data, endpoint) =>                
                println(result)
                this.accountNumber = result
                this.getPin("enter-pin")
        }        
    }

    def getPin(fileName: String) = {
        rpc(AgiGetData(fileName, "", "4")) match {
            case AgiResponse(result, data, endpoint) =>                
                println(result)
                this.pin = result
                if(this.validate(this.accountNumber, this.pin))
                    this.getChequeNumber("enter-cheque-number")
        }
    }

    def getChequeNumber(fileName: String) = {
        rpc(AgiGetData(fileName, "", "")) match {
            case AgiResponse(result, data, endpoint) =>
                println(result)
                this.chequeNumber = result
                this.getConfirmationStatus("play-options")
        }
    }

    def getConfirmationStatus(fileName: String) = {
        rpc(AgiGetData(fileName,  "", "")) match {
            case AgiResponse(result, data, endpoint) =>
                println(result)
                Integer.parseInt(result) match {
                    case 1 => 
                        //konfirm
                    case 2 =>
                        //cancel
                    case 3 =>
                        //konfirm and call
                    case 4 =>
                        //cancel and call
                    case _ =>
                        session ! CloseSession
                }
                updateDB()
        }
    }

    def validate(account_number: String, pin: String): boolean = {
        return true
    }

    def updateDB(): boolean = {
        session ! CloseSession
        this.exit()
        return true
    }
  
    def rpc(request: AgiRequest): AgiResponse = {
        this.session ! request
        receive {
            case agiRequest: AgiResponse =>
                return agiRequest
        }
    }
}
