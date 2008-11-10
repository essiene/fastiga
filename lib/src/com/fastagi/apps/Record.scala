package com.fastagi.apps

import java.io.File

import scala.actors.Actor
import scala.actors.Actor._
import com.fastagi.Session
import com.fastagi.AgiTrait
import com.fastagi.util.PropertyFile
import com.konfirmagi.webservice.WebService
import com.fastagi.apps.common.Common

class Record(session: Session) extends Actor with AgiTrait {
    
    val prop = PropertyFile.loadProperties("/etc/fastagi/agi.properties")
    val speechPath = PropertyFile.getProperty(prop, "agi.speech.out", "/etc/fastagi/speech/out/")
    val common = new Common(this, session, speechPath)

    def act() {
        this.begin()
    }

    def begin() = {
        remoteCall(session, AgiStreamFile(speechPath + "hello-record", "", "")) match {
            case AgiResponse("-1", data, endpos) =>
                common.quit("input-error")
            case AgiResponse("0", data, endpos) =>
                getRecorderID()
        }
    }

    def getRecorderID() = {
        common.getData("enter-session-key",
            (recorderID) =>
                getFileName(recorderID)
        )
    }

    def getFileName(recorderID: String) = {
        val webService = new WebService()

        webService.getFileName(recorderID) match {
            case "" =>
              common.quit("input-error")
            case fileName =>
              //TODO: use path.join equivalent here
              val file = new File(PropertyFile.getProperty(prop, "agi.speech.temp", "/etc/fastagi/speech/temp"), fileName)
              val fullPath = file.getAbsolutePath()
              record(recorderID, fileName, fullPath)                            
        }
    }

    def record(recorderID: String, fileName: String, fullPath: String): Unit = {        
        remoteCall(session, AgiRecordFile(fullPath, "ulaw", "#", "-1", "", "", "")) match {
            case AgiResponse("-1", data, endpos) =>
                common.quit("input-error")
            case AgiResponse("0", data, endpoint) =>
                playRecordedFile(recorderID, fileName, fullPath)
        }
    }

    def playRecordedFile(recorderID: String, fileName: String, fullPath: String): Unit = {
        remoteCall(session, AgiStreamFile(fullPath, "\"\"", "")) match {
            case AgiResponse("-1", data, endpos) =>
                common.quit("input-error")
            case AgiResponse("0", data, endpoint) =>
                getRecordingOptions(recorderID, fileName, fullPath)
        }
    }

    def getRecordingOptions(recorderID: String, fileName: String, fullPath: String) = {
        common.getData("recording-options",
            (recordingOption) =>
                recordingOption match {
                    case "3" =>
                        playRecordedFile(recorderID, fileName, fullPath)
                    case "2" =>
                        record(recorderID, fileName, fullPath)
                    case "1" =>
                        saveRecordedFile(recorderID, fileName, fullPath)
                }
         )
    }

    def saveRecordedFile(recorderID: String, fileName: String, fullPath: String) = {
        val webService = new WebService()

        if(webService.saveRecordedFile(recorderID, fileName, fullPath, speechPath)) {
            common.quit("recording-success")
        } else {
            common.quit("recording-fail")
        }
    }
}
