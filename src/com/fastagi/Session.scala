package com.fastagi

import scala.actors.Actor
import scala.actors.Actor._
import java.net.Socket

import com.fastagi.util._

class Session(client: Socket, appServer: AppServer) extends Actor {
    
    var application: Actor = null

    def act() {
        val pipe = new Pipe(client)
        val scriptName = pipe.get("sname")
        appServer ! new App(scriptName, this)

        loop {
            react {
                case AppInstance(application) =>
                    if (this.application == null) {
                      this.application = application
                      this.application.start()
                    }

                case agiRequest: AgiRequest => 
                    pipe.send(agiRequest.command)
                    sender ! AgiResponse(agiRequest, pipe.recieve())

                case CloseSession =>                                     
                    pipe.close()
                    if (this.application != null) 
                      this.application ! CloseSession 
                    exit()
            }
        }
    }
}
