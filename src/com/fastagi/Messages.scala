package com.fastagi

import scala.actors.Actor

case class AgiResponse(request: AgiRequest, response: String)
case class AgiRequest(command: String)

case class App(name: String, session: Session)

case class AppInstance(application: Actor)

case class CloseSession
