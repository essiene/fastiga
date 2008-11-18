package com.fastagi.apps.common

import com.fastagi.AgiTrait
import com.fastagi.Session
import com.fastagi.Messages
import java.io.File

class Common(agiApp: AgiTrait, session:Session, speechPath: String ) {

    def getData(fileName: String, errorFile: String, func: String => unit) = {
        //val file = new File(speechPath, fileName)
        agiApp.remoteCall(session, AgiGetData(fileName, "", "")) match {
            case AgiResponse("-1", data, endpoint) =>
                quit(errorFile)
            case AgiResponse(result, data, endpoint) =>
                func(result)
        }
    }

    def quit(messageFile: String): Unit = {
        //val file = new File(speechPath, messageFile)
        agiApp.remoteCall(session, AgiStreamFile(messageFile, "", ""))
        quit()
    }

    def quit(): Unit = {
        session ! CloseSession
    }
}
