package com.fastagi.util

import java.io._
import java.util._
import java.net._

class Pipe(client: Socket) {    
    val in = client.getInputStream()
    val out = client.getOutputStream()
    val headers = new Hashtable[String, String]()

    def get(key: String) = headers.get(key)

    def close() = client.close()

    def recieve(): String = {        
        val reader = new BufferedReader(new InputStreamReader(this.in))
        val line = reader.readLine()
        return line
    }
    
    def readHeader(): Unit = {
        var line = this.recieve()
        
        var header = new Vector[String]()
        while(line != "") {
            header.add(line)
            line = this.recieve()
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

    def echo(command: String): Unit = {
        val toSend = command + "\n"
        out.write(toSend.getBytes())
        out.flush()
    }

    def send(command: String): Unit = {
        val toSend = command + "\n"
        out.write(toSend.getBytes())
        out.flush()      
    }
}
