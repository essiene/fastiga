package com.fastagi.util

import java.io._
import java.net._
import java.util.Hashtable

class Pipe(client: Socket) {    
    val in = client.getInputStream()
    val out = client.getOutputStream()
    val headers = new Hashtable[String, String]()

    this.readHeader()

    def get(key: String) = headers.get(key)

    def close() = client.close()

    def recieve(): String = {        
        val reader = new BufferedReader(new InputStreamReader(this.in))
        val line = reader.readLine()
        return line
    }
    
    def readHeader(): Unit = {
        var line = this.recieve()
        
        var header = List[String]()
        while(line != "") {
            header = header ::: List(line)
            line = this.recieve()
        }

        header.foreach(this.getKeyValue)
        println(this.headers)
    }

    def getKeyValue(line: String) {       
        val key = line.substring(0, line.indexOf(":"))
        val value = line.substring((line.indexOf(":") + 1), line.length())
        this.headers.put(key, value)
    }

    def send(command: String): Unit = {
        val toSend = command + "\n"
        out.write(toSend.getBytes())
        out.flush()      
    }
}
