package com.fastagi

import scala.actors.Actor
import scala.actors.Actor._

class PinMan(session: Session) extends Actor {
    
    def act() {
        session ! new Messages("GET DATA")
        session ! new Messages("LOAD FILE")
        session ! new Messages("QUIT")
        loop {
            react {
                case Response("LOAD FILE",data) =>
                    println(sender + ":" + data)
                case Response("GET DATA", data) =>
                    println(sender + ":" + data)
                case _ =>
                    println("Unknown Response")
            }                
        }
    }
}
