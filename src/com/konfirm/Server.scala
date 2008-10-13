package com.konfirm

import java.net._
import java.io._
import com.libraries._

class Server(port: Int) extends Thread {
	val server: ServerSocket = new ServerSocket(port)
    var client: Socket = null
    val functions: AgiFunctions = new AgiFunctions()

    var agi: KonfirmAgi = null
    var config: Config = new Config(null)

	def this() = this(4573)

    var stopThread: boolean = true
	
	override def run(): Unit = {
        while(stopThread) {
			println("Konfirm: Waiting for connection....")
			client = server.accept();
			println("Konfirm: Connected......Processing Request")

            val is: InputStream = client.getInputStream()         

			val service = new ServiceClient(client, functions)

            functions.getAsteriskHeaders(is)

            agi = new KonfirmAgi(service, config)
        }
	}

    def stopListening(): Unit = {
        if(!server.isClosed()) {
            stopThread = false
            this.stop()
            client.close();
            server.close();
        }
    }
}
