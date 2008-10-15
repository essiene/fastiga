package com.fastagi

import scala.actors.Actor
import scala.actors.Actor._
import java.io._
import java.util._
import java.net._

class Session(client: Socket) extends Actor {
    
    val is = client.getInputStream()
    val os = client.getOutputStream()

    def act() {
        println("reading headers")
        this.readHeader()
        println("headers read successfully")
        val pinman = new PinMan(this)
        pinman.start()
        loop {
            react {
                case Messages(data) =>
                data match {
                    case "QUIT" =>
                        println("Closing.......")
                        client.close()
                        println("Socket Closed")
                        exit()
                    case _ =>
                        this.write(data.toString())
                        sender ! new Response(data, this.read())
                }
                case _ => 
                    this.echo("Unknown Request Format")
            }
        }
    }

    def echo(data: String): Unit = {
        val toSend = data + "\n"
        os.write(toSend.getBytes())
        os.flush()
    }

    def write(data: String): Unit = {
        val toSend = data + "\n"
        os.write(toSend.getBytes())
        os.flush()      
    }

    def read(): String = {
        val reader = new BufferedReader(new InputStreamReader(this.is))
        val line = reader.readLine()
        return line
    }

    def readHeader(): Unit = {
        val reader = new BufferedReader(new InputStreamReader(this.is))
        var line = reader.readLine()
        
        var header = new Vector[String]()
        while(line != "") {
            header.add(line)
            line = reader.readLine()
        }
        this.echo(header.toString())            
    }
}
