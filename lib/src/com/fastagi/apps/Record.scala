package com.fastagi.apps

import scala.actors.Actor
import scala.actors.Actor._
import com.fastagi.Session
import com.fastagi.AgiTrait
import com.fastagi.util.AgiUtils

class Record(session: Session) extends Actor with AgiTrait {
    
    var fileName = ""  

    def act() {
        if(this.canRecord)
            this.start("hello")
        else {
            AgiUtils.playFile("can-not-record", this)
            session ! CloseSession
        }
    }

    def start(fileName: String): Unit = {
        rpc(AgiStreamFile(fileName, "", "")) match {        
            case AgiResponse(result, data, endpoint) =>
                this.startRecord()
        }                
    }

    def canRecord(): boolean = {
        this.fileName = this.getFileName()
        true
    }

    def getFileName(): String = {
       return "enter-pin" 
    }

    def startRecord() = {        
        rpc(AgiRecordFile(this.fileName, "ulaw", "#", "-1", "", "", "")) match {
            case AgiResponse(result, data, endpoint) =>
                AgiUtils.playFile(fileName, this)
                AgiUtils.getData("save-file", this) match {
                    case "1" => 
                        //move file from temp location to original location
                        session ! CloseSession
                    case "2" =>
                        this.restart("hello")
                }                
        }
    }

    def restart(fileName: String) = this.start(fileName)         

    override def rpc(request: AgiRequest): AgiResponse = {
        this.session ! request
        receive {
            case agiRequest: AgiResponse =>
                return agiRequest
        }
    }
}
