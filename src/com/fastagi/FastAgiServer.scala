package com.fastagi

import java.net._

class FastAgiServer(port: Int) extends Thread {
    val server: ServerSocket = new ServerSocket(port)
    var session: Session = null

    override def run(): Unit = {
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