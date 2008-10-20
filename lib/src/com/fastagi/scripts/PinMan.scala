package com.fastagi.scripts

import scala.actors.Actor
import scala.actors.Actor._

import com.fastagi.Session

class PinMan(session: Session) extends Actor {
    
    var agiRequests = List[AgiRequest]()
    var accountNumber = ""
    var pin = ""
    var any = ""
    var first = new AgiRequest("START")
    
    def act() {
        this.prepareRequests()
        session ! first
        loop {
            react {
                case agiResponse:AgiResponse =>                    
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
        agiRequests = agiRequests ::: List(new AgiStreamFile("hello", "", ""))
        agiRequests = agiRequests ::: List(new AgiGetData("enter-your-account-number", "-1", "10"))     
        agiRequests = agiRequests ::: List(new AgiGetData("enter-your-pin", "-1", "4"))     
        agiRequests = agiRequests ::: List(new AgiGetData("silence/10", "-1", "1"))       
        agiRequests = agiRequests ::: List(new AgiRequest("HANG UP"))
    }

    /**
        Would prefer to call the commands directly instead of calling
            return (commandNumber). It kinda sucks trying to figure which
            command is which, even for this four commands
    */

    def parseResponse(agiResponse: AgiResponse): Int = {
        val request = agiResponse.request
        request match {
            case AgiRequest("START") => return 0

            case AgiStreamFile(fileName, escapeDigits, sampleOffset) =>            
                if(!agiResponse.response.equals("-1")) 
                    return 1 //account number
                return 4 //hangup

            case AgiGetData("enter-your-account-number", timeout, maxDigits) =>
                this.accountNumber = this.accountNumber + agiResponse.response
                println(this.accountNumber)
                return 2 //pin

            case AgiGetData("enter-your-pin", timeout, maxDigits) =>
                this.pin = this.pin + agiResponse.response
                println(this.pin)
                return 3 //silence/10

            case AgiGetData("silence/10", timeout, maxDigits) =>
                if(!agiResponse.response.equals("")) {
                    this.any = this.any + agiResponse.response
                    return 3
                } else {
                    println(this.any)
                    return 4
                }

            case _ =>
                return -1
        }
    }
}
