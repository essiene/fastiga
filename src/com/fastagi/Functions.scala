package com.fastagi

import java.io._
import java.util._

class Functions(is: InputStream, os: OutputStream) {

    def echo(data: String): Unit = {
        val toSend = data
        os.write(toSend.getBytes())
        os.flush()
    }

    def readHeader() {
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
