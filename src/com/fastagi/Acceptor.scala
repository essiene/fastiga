package com.fastagi

import scala.actors.Actor
import scala.actors.Actor._
import java.io._
import java.net._

class Acceptor(port: Int, appServer:AppServer) extends Actor {

    val server = new ServerSocket(port)
    val sessions = List[Session]()
    var isAlive = true

    def act() {
        while(isAlive) {
//            println("Waiting for Connection....")
            val client = server.accept()
//            println("Connected")           
            val session = new Session(client, appServer)
            session.start()
            sessions ::: List(session)
        }
    }

    def stop(): Unit = {
        this.isAlive = false
        this.sessions.foreach(s => s ! CloseSession)
        this.exit()
    }
}
