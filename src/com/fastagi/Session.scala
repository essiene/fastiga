package com.fastagi

import scala.actors.Actor
import scala.actors.Actor._
import java.net.Socket

import com.fastagi.util._

class Session(client: Socket, appServer: AppServer) extends Actor {
    
    val pipe = new Pipe(client)
    var application: Actor = null

    def act() {
        pipe.readHeader()

        val scriptName = pipe.get("sname")
        appServer ! new App(scriptName, this)

        loop {
            react {
                case AppInstance(application) =>
                    if (this.application == null) 
                      this.application = application

                case agiRequest: AgiRequest => 
                    pipe.send(agiRequest.command)
                    sender ! new AgiResponse(agiRequest, pipe.recieve())

                case CloseSession =>                    
                    pipe.close()
                    if (this.application != null) 
                      this.application ! new CloseSession("Socket Closed") 
                    exit()
            }
        }
    }
}
