package com.konfirm

import java.io._
import java.net._
import java.util.Vector

class ServiceClient(client: Socket, functions: AgiFunctions) {
	
	val cis: InputStream  = client.getInputStream()
	val cos: OutputStream = client.getOutputStream()

	def sendRequest(request: String): Unit = {
        println("Sending request: " + request)
        cos.write(request.getBytes())
        cos.flush()
	}

    def getRawResponse(): String = {
        var ch: Int = 0;
        val sb: StringBuffer = new StringBuffer()
       
        val inVector: Vector[Int] = new Vector()
        var streamHasMore = true
        while(streamHasMore) {          
            ch = cis.read()
            inVector.add(ch)

            sb.append(ch.asInstanceOf[char])

            if(functions.isEOF(inVector, 1)) streamHasMore = false
            if(ch == -1) streamHasMore = false           
        }
        sb.toString()
    }

    def getParsedResponse(response: String): String = {
        try {
            return response.substring(response.indexOf("=") + 1, response.length() - 1)
        } catch {
            case e: StringIndexOutOfBoundsException => return null
        }
    }

    def hangUp(): Unit = {
        sendRequest("Hangup\n")
        val parsed = getParsedResponse(getRawResponse())
        println("---Hangup Response---\n" + parsed)
    }

    def getServiceType(): Unit = {
        
    }
}
