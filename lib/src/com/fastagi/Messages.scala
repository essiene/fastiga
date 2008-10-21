package com.fastagi

import scala.actors.Actor

case class AgiRequest(var command: String)

case class AgiResponse(result: String, data: String, endpoint: String) {
    def this(result: String, data: String) = this(result, data, "0")
    def this(result: String) = this(result, null, "0")
}

case class App(name: String, session: Session)

case class AppInstance(application: Actor)

case class CloseSession

case class AgiStreamFile(fileName: String, escapeDigits: String, sampleOffset: String) extends AgiRequest("") {    
    this.command = "STREAM FILE " + fileName + " " + escapeDigits + " " + sampleOffset + "\n"
}

case class AgiGetData(fileName: String, timeout: String, maxDigits: String) extends AgiRequest("") {
    this.command = "GET DATA " + fileName + " " + timeout + " " + maxDigits + "\n"
}
