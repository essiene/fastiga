package com.fastiga.apps //All Fastiga Apps must be in this package. And can be deployed whereever scala will find them

import scala.actors.Actor
import scala.actors.Actor._

import com.fastiga._

class Example(session: Session) extends Actor with AgiTrait {
    


    def act() {
        this.begin()
    }

    def begin() = {
        remoteCall(session, AgiStreamFile("application-greeting", "", "")) match {
            case AgiResponse("-1", data, endpos) =>
                //supply full path to an error file to play
                quit("input-error")
            case AgiResponse("0", data, endpos) =>
                getUserId()
        }
    }

    def getUserId() = {
        this.getData("digit/10", "tt-weasels", 
            (userId) =>
                sendToWebService(userId)
        )
    }

    def sendToWebService(userId: String) = {
        remoteCall(session, AgiGetChannelVariable("WebServiceID")) match {
            case AgiResponse("0", data, endpos) =>
                quit()
            case AgiResponse("1", webServiceId, endpos) =>
               //webservice.call(webServiceId, userId)
               quit("goodbye")
        }
    }

    // Utility functions that should be in fastiga eventually
    def quit(messageFile: String): Unit = {
        remoteCall(session, AgiStreamFile(messageFile, "", ""))
        quit()
    }

    def quit(): Unit = {
        session ! CloseSession
    }

    def getData(fileName: String, errorFile: String, func: String => unit) = {
        remoteCall(session, AgiGetData(fileName, "", "")) match {
            case AgiResponse("-1", data, endpoint) =>
                quit(errorFile)
            case AgiResponse(result, data, endpoint) =>
                func(result)
        }
    }

}
