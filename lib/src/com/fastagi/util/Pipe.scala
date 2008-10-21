package com.fastagi.util

import java.io._
import java.net._
import com.konfirmagi.parser._
import java.util.Hashtable

class Pipe(client: Socket) {    
    val in = client.getInputStream()
    val out = client.getOutputStream()
    val headers = new Hashtable[String, String]()
    val reader = new BufferedReader(new InputStreamReader(this.in))

    this.readHeader()

    def get(key: String) = headers.get(key)

    def close() = client.close()

    def receive():AgiResponse  = {        
        val line = this.reader.readLine()
        return parse(line)
    }

    def parse(line: String): AgiResponse = {
        val parser = new Parser(new StringReader(line))
        val table = parser.parseOneLine()
        return new AgiResponse(table.get("result"), table.get("data"), table.get("endpoint"))
    }
    
    def readHeader(): Unit = {
        var line = this.reader.readLine() 
        
        var header = List[String]()
        while(line != "") {
            header = header ::: List(line)
            line = this.reader.readLine()
        }

        header.foreach(this.getKeyValue)
        println(this.headers)
    }

    def getKeyValue(line: String) {       
        val key = line.substring(0, line.indexOf(":"))
        val value = line.substring((line.indexOf(":") + 1), line.length())
        this.headers.put(key, value.trim)
    }

    def send(command: String): Unit = {
        val toSend = command + "\n"
        out.write(toSend.getBytes())
        out.flush()      
    }
}
