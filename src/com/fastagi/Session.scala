package com.fastagi

import scala.actors.Actor
import scala.actors.Actor._
import java.net._
import java.io._
import java.util._

class Session(client: Socket) extends Actor {
    
    val is: InputStream = client.getInputStream()
    val os: OutputStream = client.getOutputStream()

    readHeader()
    def act() {
        loop {
            react {
                case Messages(data) =>
                    data match {
                        case "QUIT" =>
                            println("Closing Socket....")
                            client.close()
                            println("Socket Closed.....")
                            exit()
                        case _ =>                            
                            //send message thru client

                            //read response from socket                                   
                            //send back response to sender
                    }
                case _ =>
                    //Send back unknown command response
            }
        }
    }

    def readHeader() {
        new Thread() {
            override def run(): Unit = {
                var ch: Int = 0
                var sb: StringBuffer = new StringBuffer()
                val charSequence: Vector[Int] = new Vector()
                var streamHasMore = true
                while(streamHasMore) {
                    ch = is.read()
                    val thisChar: Char = ch.asInstanceOf[char]
                    sb.append(thisChar)
                    charSequence.add(ch)
                    if(isEOHeader(charSequence)) streamHasMore = false
                    if(ch == -1) streamHasMore = false
                }
                println("----------Recieved Headers-------\n" + sb.toString()) 
                val toSend = "Happy Actors\r\n"
                os.write(toSend.getBytes())
                os.flush()
            }
        }.start()
    }

    def isEOHeader(charSequence: Vector[Int]): boolean = {
       val size =  charSequence.size()
       if(size < 4) return false

       if(size >= 4) {
            if( (charSequence.get(size - 4) == 13) && 
                (charSequence.get(size - 3) == 10) && 
                (charSequence.get(size - 2) == 13) && 
                (charSequence.get(size - 1) == 10) ) return true
            if( (charSequence.get(size - 2) == 10) &&
                (charSequence.get(size - 1) == 10) ) return true                    
            else return false                
       }
       return false
    }

    def isEOF(charSequence: Vector[Int]) {
    }
}
