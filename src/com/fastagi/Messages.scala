package com.fastagi

case class AgiResponse(request: Request, response: String)
case class AgiRequest(command: String)

case class App(name: String, session: Session)
