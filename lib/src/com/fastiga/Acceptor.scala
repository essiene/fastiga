package com.fastiga

import scala.actors.Actor
import scala.actors.Actor._
import java.io._
import java.net._

class Acceptor(port: Int, appServer:AppServer) extends Actor {

    val server = new ServerSocket(port)
    var isAlive = true

    def act() {
        while(isAlive) {
            val client = server.accept()
            val session = new Session(client, appServer)
            session.start()
        }
    }

    def close(): Unit = {
        this.isAlive = false
        server.close()
    }
}
