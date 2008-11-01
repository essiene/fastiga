package com.fastagi.apps

import scala.actors.Actor
import scala.actors.Actor._
import com.fastagi.Session
import com.fastagi.AgiTrait
import com.fastagi.util.PropertyFile
import com.konfirmagi.webservice.WebService

class Record(session: Session) extends Actor with AgiTrait {
    
    val prop = PropertyFile.loadProperties("/etc/fastagi/agi.properties")
    val speechPath = PropertyFile.getProperty(prop, "agi.speech.out")

    def getData(fileName: String, func: String => Unit) = {
      remoteCall(session, AgiGetData(speechPath + fileName, "", "")) match {
        case AgiResponse("-1", data, endpoint) =>
            quit("input-error")
        case AgiResponse(result, data, endpoint) =>
            func(result)
      }
    }

    def quit(messageFile: String): Unit = {
      remoteCall(session, AgiStreamFile(speechPath + messageFile, "\"\"", ""))
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
        remoteCall(session, AgiStreamFile(speechPath + "hello-record", "\"\"", "")) match {
            case AgiResponse("-1", data, endpos) =>
                quit("input-error")
            case AgiResponse("0", data, endpos) =>
                getRecorderID()
        }
    }

    def getRecorderID() = {
        getData("enter-recorder-id",
            (recorderID) =>
                getFileName(recorderID)
        )
    }

    def getFileName(recorderID: String) = {
        val webService = new WebService()

        webService.getFileName(recorderID) match {
            case "" =>
              quit("input-error")
            case fileName =>
              //TODO: use path.join equivalent here
              val fullPath = PropertyFile.getProperty(prop, "agi.speech.temp") + fileName
              record(recorderID, fileName, fullPath)                            
        }
    }

    def record(recorderID: String, fileName: String, fullPath: String): Unit = {        
        remoteCall(session, AgiRecordFile(fullPath, "ulaw", "#", "-1", "", "", "")) match {
            case AgiResponse("-1", data, endpos) =>
                quit("input-error")
            case AgiResponse("0", data, endpoint) =>
                playRecordedFile(recorderID, fileName, fullPath)
        }
    }

    def playRecordedFile(recorderID: String, fileName: String, fullPath: String): Unit = {
        remoteCall(session, AgiStreamFile(fullPath, "\"\"", "")) match {
            case AgiResponse("-1", data, endpos) =>
                quit("input-error")
            case AgiResponse("0", data, endpoint) =>
                getRecordingOptions(recorderID, fileName, fullPath)
        }
    }

    def getRecordingOptions(recorderID: String, fileName: String, fullPath: String) = {
        getData("recording-options",
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
            quit("thank-you")
        } else {
            quit("input-error")
        }
    }
}
