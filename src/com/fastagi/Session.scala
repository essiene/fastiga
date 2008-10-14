package com.fastagi

import scala.actors.Actor
import scala.actors.Actor._
import java.net._
import java.io._
import java.util._

class Session(client: Socket) extends Actor {
    
    val is = client.getInputStream()
    val os = client.getOutputStream()
    val functions = new Functions(this.is, this.os)

    def act() {
        this.functions.readHeader()
        loop {
            react {
                case Messages(data) =>
                    data match {
                        case "QUIT" =>
                            println("Closing Socket....")
                            client.close()
                            println("Socket Closed.....")
                            exit()
                    }
                case _ => 
                    this.functions.echo("Unknown Message")
            }
        }
    }
}
