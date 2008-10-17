package com.fastagi

import scala.actors.Actor

case class AgiRequest(command: String)
case class AgiResponse(req: AgiRequest, res: String) {
    def parse(): String {
        if(res.startsWith("200") 
            return res.substring("result", res.length())
        return "result=other"
    }
}


case class App(name: String, session: Session)

case class AppInstance(application: Actor)

case class CloseSession

case class AgiStreamFile(fileName: String, escapeDigits: String, sampleOffset: String)
case clases ResponseAgiStreamFile(request: AgiStreamFile, var response: String) extends AgiResponse(response) {
    response = super.parse
}
