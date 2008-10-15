package com.fastagi

import scala.actors.Actor
import scala.actors.Actor._
import java.io._
import java.util._
import java.net._

class Acceptor(port: Int) extends Actor {

    val server = new ServerSocket(port)
    val sessionList = new Vector[Socket]()

    def act() {
        while(true) {
            println("Waiting for Connection....")
            val client = server.accept()
            println("Connected")           
            val session = new Session(client)
            sessionList.add(client)
            session.start()
        }
    }

    def stopListening(): Unit = {
        val elements = sessionList.elements()
        try {
            while(elements.hasMoreElements()) {
                val a = elements.nextElement()
                a.close()
            }   
            server.close()
        } catch {
            case e:Exception => e.printStackTrace()
        }
    }
}
