package com.fastagi

import scala.actors.Actor
import scala.actors.Actor._

class AppServer(appPackage: String) extends Actor {
    def act() {
        loop {
            react {
                case App(name, session:Session) =>
                    try {
                        val agiApp = this.getApp(name, session)
                        sender ! AppInstance(agiApp)                        
                    } catch {
                        case e:Exception => e.printStackTrace()                  
                                session ! CloseSession                      
                    }
                case _ =>
                    //Unknown Message Type
            }
        }
    }

    def getApp(appName: String, session: Session): Actor = {
        val agiAppClass = Class.forName(this.appPackage + "." + appName)                        
        val constructor = agiAppClass.getConstructor(Array(session.getClass))
        constructor.newInstance(Array(session)).asInstanceOf[Actor]
    }
}
