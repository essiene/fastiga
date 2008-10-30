package com.fastagi

import scala.actors.Actor
import scala.actors.Actor._
import scala.actors.Exit

class ServerMonitor(appServer:AppServer) extends Actor {

    var isAlive = true

    def act() {
        loop {
            react {
                case Exit(actor, reason) =>
                    this.appServer.trapExit = false                    
                    this.appServer.start()
            }
        }
    }
}
