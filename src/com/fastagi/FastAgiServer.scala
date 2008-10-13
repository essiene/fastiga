package com.fastagi

import scala.actors.Actor
import scala.actors.Actor._

class FastAgiServer() extends Actor {
    def act() {
        loop {
            react {
                case Messages(data) =>
                    data match {
                        case null => sender ! Messages("Call with ?action=actionName")
                        case _ => sender ! Messages("\nRequest: " + data + "\nResponse: Hello Servlet-Actor World\n")
                    }
                case _ =>
                    println("Server: I recieve an unknown message....nothing to do")
            }
        }
    }
}	
