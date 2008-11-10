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
    this.command = "STREAM FILE " + fileName + " \"" + escapeDigits + "\" " + sampleOffset
}

case class AgiGetData(fileName: String, timeout: String, maxDigits: String) extends AgiRequest("") {
    this.command = "GET DATA " + fileName + " " + timeout + " " + maxDigits
}

case class AgiGetChannelVariable(varName: String) extends AgiRequest("") {
    this.command = "GET VARIABLE " + varName
}

case class AgiRecordFile(fileName: String, 
                         format: String, 
                         esc_digits: String, 
                         timeout: String, 
                         offset: String, 
                         beep: String, 
                         silence: String) extends AgiRequest("") {
    this.command = "RECORD FILE " + 
                    fileName + " " + 
                    format + " " + 
                    esc_digits + " " + 
                    timeout + " " + 
                    offset + " " + 
                    beep + " " + 
                    silence

    def this(fileName: String, format: String, esc_digits: String, timeout: String) = this(fileName, format, esc_digits, timeout, "", "", "")
}
