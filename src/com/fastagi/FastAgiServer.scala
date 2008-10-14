package com.fastagi

import java.net._
import scala.actors.Actor

class FastAgiServer(port: Int) extends Actor {
    val server: ServerSocket = new ServerSocket(port)
    var session: Session = null

    def act() = {
        while(true) {
            println("Waiting for connection")
            val client: Socket = server.accept()
            println("Connected")
            session = new Session(client)
            session.start()          
        }
    }

    def stopListening(): Unit = {
        server.close()
        println("Connection Closed")
    }
}	
