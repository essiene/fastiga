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
    
    val common = new Common(this, session, "")
    val errorFile = common.getFullPath("input-error", "recorded", "")

    def act() {
        this.begin()
    }

    def begin() = {
        val filePath = common.getFullPath("hello-record", "recorded", "record")

        filePath match {
            case "" =>
                common.quit(errorFile)
            case file =>
                remoteCall(session, AgiStreamFile(file, "", "")) match {
                    case AgiResponse("-1", data, endpos) =>
                        common.quit(errorFile)
                    case AgiResponse("0", data, endpos) =>
                        getRecorderID()
                }
        }
    }

    def getRecorderID() = {
        val filePath = common.getFullPath("enter-session-key", "recorded", "record")
        filePath match {
            case "" =>
                common.quit(errorFile)
            case file =>            
                common.getData(filePath, errorFile, 
                    (recorderID) =>
                        getFileName(recorderID)
                )
        }
    }

    def getFileName(recorderID: String) = {
        val webService = new WebService()

        webService.getFileName(recorderID) match {
            case "" =>
              common.quit(errorFile)
            case fileName =>
              val file = new File(common.getFullPath("", "temp", "record"), fileName)
              println("FILE: " + file)
              val fullPath = file.getAbsolutePath()
              getAppName(recorderID, fileName, fullPath)
        }
    }

    def getAppName(recorderID: String, fileName: String, fullPath: String) = {
        val webService = new WebService()

        webService.getAppName(recorderID) match {
            case "" =>
              common.quit(errorFile)
            case appName =>
              record(recorderID, fileName, fullPath, appName)           
        }
    }

    def record(recorderID: String, fileName: String, fullPath: String, appName: String): Unit = {        
        remoteCall(session, AgiRecordFile(fullPath, "ulaw", "#", "-1", "", "", "")) match {
            case AgiResponse("-1", data, endpos) =>
                common.quit(errorFile)
            case AgiResponse("0", data, endpoint) =>
                playRecordedFile(recorderID, fileName, fullPath, appName)
        }
    }

    def playRecordedFile(recorderID: String, fileName: String, fullPath: String, appName: String): Unit = {
        remoteCall(session, AgiStreamFile(fullPath, "", "")) match {
            case AgiResponse("-1", data, endpos) =>
                common.quit(errorFile)
            case AgiResponse("0", data, endpoint) =>
                getRecordingOptions(recorderID, fileName, fullPath, appName)
        }
    }

    def getRecordingOptions(recorderID: String, fileName: String, fullPath: String, appName: String) = {
        val filePath = common.getFullPath("recording-options", "recorded", "record")

        filePath match {
            case "" =>
                common.quit(errorFile)
            case file =>
                common.getData(file, errorFile, 
                    (recordingOption) =>
                        recordingOption match {
                            case "3" =>
                                playRecordedFile(recorderID, fileName, fullPath, appName)
                            case "2" =>
                                record(recorderID, fileName, fullPath, appName)
                            case "1" =>
                                saveRecordedFile(recorderID, fileName, fullPath, appName)
                        }
                )
        }
    }

    def saveRecordedFile(recorderID: String, fileName: String, fullPath: String, appName: String) = {
        val successPath = common.getFullPath("recording-success", "recorded", "record")
        val failPath = common.getFullPath("recording-fail", "recorded", "record")

        val webService = new WebService()

        webService.saveRecordedFile(recorderID, fileName, fullPath, appName) match {
            case true =>
                common.quit(successPath)
            case false =>
                common.quit(failPath)
        }
    }
}
