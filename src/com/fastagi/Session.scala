package com.fastagi

import scala.actors.Actor
import scala.actors.Actor._
import java.io._
import java.util._
import java.net._

class Session(client: Socket, appServer: AppServer) extends Actor {
    
    val is = client.getInputStream()
    val os = client.getOutputStream()
    val dico = new Hashtable[String, String]()

    def act() {
        this.readHeader()

        val scriptName = this.dico.get("sname")
        scriptName match {
            case "pinman" =>
                appServer ! new Request("New", "PinMan", this)
            case "konfirm" =>
                appServer ! new Request("New", "Konfirm", this)
            case "prekonfirm" =>
                appServer ! new Request("New", "PreKonfirm", this)
            case "record" => 
                appServer ! new Request("New", "Record", this)
            case _ =>
                appServer ! new Request("New", "Konfirm", this)
        }

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
                this.dico.put(key, value)
            }                
        }
        println(dico.toString())
    }
}
