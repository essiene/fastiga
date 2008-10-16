package com.fastagi.scripts

import scala.actors.Actor
import scala.actors.Actor._

import com.fastagi.Session

class PinMan(session: Session) extends Actor {
    
    var agiRequests = List[AgiRequest]()
    var first = new AgiRequest("START")
    
    def act() {
        this.prepareRequests()
        session ! first
        loop {
            react {
                case agiResponse:AgiResponse =>                    
                    println(sender + ": " + agiResponse.response)
                    var nextRequest = this.parseResponse(agiResponse)
                    if(nextRequest != -1) session ! agiRequests(nextRequest)
                    else session ! CloseSession

                case CloseSession =>
                    exit()

                case _ =>
                    println("Unknown Response")
            }                
        }
    }

    def prepareRequests() {
        agiRequests = agiRequests ::: List(new AgiRequest("GET DATA"))
        agiRequests = agiRequests ::: List(new AgiRequest("LOAD FILE"))
        agiRequests = agiRequests ::: List(new AgiRequest("PLAY FILE"))
        agiRequests = agiRequests ::: List(new AgiRequest("HANG UP"))
    }

    def parseResponse(agiResponse: AgiResponse): Int = {
        val request = agiResponse.request
        val data = "1234"
        request.command match {
            case "START" => return 1
            case "LOAD FILE" =>
                if(agiResponse.response == "1") return 0
                else return 3
            case "GET DATA" =>
                if(agiResponse.response.equals(data)) return 2
                else return 3
            case "PLAY FILE" =>
                if(agiResponse.response == "1") return 3
                else return -1
            case "HANG UP" => 
                return -1                
            case _ =>
                return -1
        }
    }
}
