package com.fastiga.apps

import scala.actors.Actor
import scala.actors.Actor._

import com.fastiga.Session
import com.fastiga.AgiTrait
import com.fastiga.Messages

class Example(session: Session) extends Actor with AgiTrait {
    

    def quit(messageFile: String): Unit = {
        remoteCall(session, AgiStreamFile(messageFile, "", ""))
        quit()
    }

    def quit(): Unit = {
        session ! CloseSession        
    }

    def getData(fileName: String, errorFile: String, func: String => unit) = {
        remoteCall(session, AgiGetData(fileName, "", "")) match {
            case AgiResponse("-1", data, endpoint) =>
                quit(errorFile)
            case AgiResponse(result, data, endpoint) =>
                func(result)
        }
    }

    def act() {
        this.begin()
    }

    def begin() = {
        remoteCall(session, AgiStreamFile("tt-monkeys", "", "")) match {        
            case AgiResponse("-1", data, endpos) =>
                //supply full path to an error file to play
                quit("tt-weasels")
            case AgiResponse("0", data, endpos) =>
                getSomeDTMF()
        }
    }

    def getSomeDTMF() = {
        this.getData("digit/10", "tt-weasels", 
            (someDTMF) =>            
                getSomeChannelVariable(someDTMF)
        )
    }

    def getSomeChannelVariable(aDTMF: String) = {
        remoteCall(session, AgiGetChannelVariable("achanvar")) match {
            case AgiResponse("0", data, endpos) =>
                quit()
            case AgiResponse("1", theChanVar, endpos) =>
               println("Channel Var: " + theChanVar) 
               println("DTMF Digit: " + aDTMF)
               quit("goodbye")
        }
    }
}
