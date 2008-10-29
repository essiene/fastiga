package com.fastagi.apps

import scala.actors.Actor
import scala.actors.Actor._
import com.fastagi.Session
import com.fastagi.AgiTrait

class Record(session: Session) extends Actor with AgiTrait {
    
    var fileName = ""  
    var recordKey = ""

    def act() {
        this.start("hello-get-record-key")
    }

    def start(fileName: String): Unit = {
        this.recordKey = this.agiUtils.getData(fileName, this)
        if(!this.recordKey.equals("-1")) {
            this.getFileToRecord()
            this.startRecord()
        } else {
            session ! CloseSession
        }
    }

    def startRecord() = {        
        rpc(AgiRecordFile(this.fileName, "ulaw", "#", "-1", "", "", "")) match {
            case AgiResponse(result, data, endpoint) =>
                agiUtils.playFile(fileName, this)
                agiUtils.getData("save-file", this) match {
                    case "1" => 
                        //move file from temp location to original location
                        val from = ""
                        val to = ""
                        var url = urlMaker.url_for("recorder", "move_file", this.recordKey, Map("from"->from, "to"->to))

                        jsonPipe.parse(url)
                        val retVal = jsonPipe.get("Status")
                        if(retVal.equals("OK")) {
                            //success
                        } else {
                            //failure
                        }
                        session ! CloseSession
                    case "2" =>
                        this.restart("hello")
                }                
        }
    }

    def getFileToRecord() = {
        var url = urlMaker.url_for("recorder", "get_file_to_record", this.recordKey, null)

        jsonPipe.parse(url)

        val retVal = jsonPipe.get("Status")
        if(retVal.equals("OK")) 
            this.fileName = jsonPipe.get("filename")        
        else {
            session ! CloseSession
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
