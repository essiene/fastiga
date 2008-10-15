package com.fastagi

import scala.actors.Actor
import scala.actors.Actor._
import java.io._
import java.util._
import java.net._

class Session(client: Socket, appServer: AppServer) extends Actor {
    
    val in = client.getInputStream()
    val out = client.getOutputStream()
    val pipe = new Pipe(client)
    val headers = new Hashtable[String, String]()

    def act() {
        this.readHeader()

        val scriptName = this.headers.get("sname")
            appServer ! new App(scriptName, this)
        }

        loop {
            react {
                case Application =>
                          this.application = ...
                case AgiRequest(command) => 
                          pipe.send(command)
                          sender ! new AgiResponse(req, pipe.recv())
                case CloseSession =>
                          pipe.close()
                          exit()
            }
        }
    }

    def echo(data: String): Unit = {
        val toSend = data + "\n"
        out.write(toSend.getBytes())
        out.flush()
    }

    def write(data: String): Unit = {
        val toSend = data + "\n"
        out.write(toSend.getBytes())
        out.flush()      
    }

    def read(): String = {
        val reader = new BufferedReader(new InputStreamReader(this.in))
        val line = reader.readLine()
        return line
    }

    def readHeader(): Unit = {
        val reader = new BufferedReader(new InputStreamReader(this.in))
        var line = reader.readLine()
        
        var header = new Vector[String]()
        while(line != "") {
            header.add(line)
            line = reader.readLine()
        }
        this.parseHeader(header);
    }

    def parseHeader(header: Vector[String]): Unit = {
        val elements = header.elements()
        while(elements.hasMoreElements()) {
            val thisElement = elements.nextElement()
            val index = thisElement.indexOf(":")
            if(index > 0) {
                val key = thisElement.substring(0, index)
                val value = thisElement.substring((index + 1), thisElement.length())
                this.headers.put(key, value)
            }                
        }
        println(headers.toString())
    }
}
