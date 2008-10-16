package com.fastagi

import scala.actors.Actor
import scala.actors.Actor._
import java.io._
import java.net._

class Acceptor(port: Int, appServer:AppServer) extends Actor {

    val server = new ServerSocket(port)
    var sessions = List[Session]()
    var isAlive = true

    def act() {
        while(isAlive) {
            val client = server.accept()
            val session = new Session(client, appServer)
            session.start()
            sessions = sessions ::: List(session)
        }
    }

    def stop(): Unit = {
        this.isAlive = false
        this.sessions.foreach(s => s ! CloseSession)
        this.exit()
    }
}
