package com.fastagi.apps.common

import com.fastagi.AgiTrait
import com.fastagi.Session
import com.fastagi.Messages
import java.io.File

class Common(agiApp: AgiTrait, session:Session, speechPath: String ) {

    def getData(fileName: String, func: String => unit) = {
        val file = new File(speechPath, fileName)
        agiApp.remoteCall(session, AgiGetData(file.getAbsolutePath(), "", "")) match {
            case AgiResponse("-1", data, endpoint) =>
                quit("input-error")
            case AgiResponse(result, data, endpoint) =>
                func(result)
        }
    }

    def quit(messageFile: String): Unit = {
        val file = new File(speechPath, messageFile)
        agiApp.remoteCall(session, AgiStreamFile(file.getAbsolutePath(), "\"\"", ""))
        quit()
    }

    def quit(): Unit = {
        session ! CloseSession
    }
}
