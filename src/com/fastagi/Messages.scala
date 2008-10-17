package com.fastagi

import scala.actors.Actor

case class AgiRequest(var command: String)

case class AgiResponse(request: AgiRequest, var response: String) {
    def parse(): String = {
        var parsed = ""
        if(response.startsWith("200")) {
            val result = response.substring(response.indexOf("result"), response.length())
            parsed = result.substring((result.indexOf("=") + 1), result.length())
        }
        return parsed.trim()
    }
}


case class App(name: String, session: Session)

case class AppInstance(application: Actor)

case class CloseSession

case class AgiStreamFile(fileName: String, escapeDigits: String, sampleOffset: String) extends AgiRequest("") {    
    this.command = "STREAM FILE " + fileName + " " + escapeDigits + " " + sampleOffset + "\n"
}

case class ResponseAgiStreamFile(req: AgiStreamFile, res: String) extends AgiResponse(req, res) {
    var parsed = super.parse
    parsed match {
        case "0" => this.response= "Playback completed with no digit pressed"
        case "-1" => this.response= "Error or Hangup"
        case _ => this.response= String.valueOf(parsed.asInstanceOf[char])
    }
}

case class AgiGetData(fileName: String, timeout: String, maxDigits: String) extends AgiRequest("") {
    this.command = "GET DATA " + fileName + " " + timeout + " " + maxDigits + "\n"
}

case class ResponseAgiGetData(req: AgiGetData, var res: String) extends AgiResponse(req, res) {
    this.response= super.parse.substring(0, Integer.parseInt(req.maxDigits))
}
