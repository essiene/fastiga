package com.fastagi.apps.common

import com.fastagi.AgiTrait
import com.fastagi.Session
import com.fastagi.Messages
import com.konfirmagi.webservice.WebService

class Common(agiApp: AgiTrait, session:Session, speechPath: String ) {

    def getData(fileName: String, errorFile: String, func: String => unit) = {
        agiApp.remoteCall(session, AgiGetData(fileName, "", "")) match {
            case AgiResponse("-1", data, endpoint) =>
                quit(errorFile)
            case AgiResponse(result, data, endpoint) =>
                func(result)
        }
    }

    def getFullPath(fileName: String, path: String): String = {
        val webService = new WebService()
        webService.getFullPath(fileName, "konfirm", path) match {
            case "" =>
                return ""
            case fullPath =>
                return fullPath
        }
    }

    def quit(messageFile: String): Unit = {
        agiApp.remoteCall(session, AgiStreamFile(messageFile, "", ""))
        quit()
    }

    def quit(): Unit = {
        session ! CloseSession
    }
}
