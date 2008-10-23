package com.fastagi.apps

import scala.actors.Actor
import scala.actors.Actor._
import com.fastagi.Session
import com.fastagi.AgiTrait
import com.fastagi.util.AgiUtils

class Konfirm(session: Session) extends Actor with AgiTrait {
    
    var accountNumber = ""
    var userPin = ""
    var dbPin = ""
    var callid = ""
    var tries = 0

    def act() {
        this.start("hello")
    }

    def start(fileName: String): Unit = {
        this.rpc(AgiStreamFile(fileName, "", "")) match {        
            case AgiResponse(result, data, endpoint) =>
                this.accountNumber = AgiUtils.getChannelVariable("extra", this)
                this.callid = AgiUtils.getChannelVariable("callid", this)

                if(!this.callid.equals("Null")) {
                    this.userPin = AgiUtils.getData("enter-pin", this)

                    if(validate(accountNumber, userPin)) {
                        this.playCachedFile(this.callid)
                    } else {
                        AgiUtils.playFile("invalid-pin-entered", this)
                        if(tries < 3) {
                            tries = tries + 1
                            this.restart("hello")
                        }
                    }

                } else {
                    session ! CloseSession
                }
        }     
    }

    def restart(fileName: String) = this.start(fileName)

    def playCachedFile(callid: String) = {
        AgiUtils.playFile(callid, this)
        this.getConfirmationStatus("play-options")
    }

    def getConfirmationStatus(fileName: String) = {
        this.rpc(AgiGetData(fileName, "", "")) match {
            case AgiResponse(result, data, endpoint) =>
                Integer.parseInt(result) match {
                    case 1 => //konfirm
                    case 2 => //cancel
                    case 3 => //konfirm and contact
                    case 4 => //cancel and contact
                    case _ =>
                        session ! CloseSession
                }
                this.updateDB()
        }
    }

    def updateDB(): boolean = {
        session ! CloseSession
        return true
    }    

    def validate(accountNumber: String, userPin: String): boolean = {
        return true
    }

    override def rpc(request: AgiRequest): AgiResponse = {
        this.session ! request
        receive {
            case agiResponse: AgiResponse =>
                return agiResponse
        }
    }
}
