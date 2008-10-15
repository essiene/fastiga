package com.fastagi

import scala.actors.Actor
import scala.actors.Actor._

class AppServer extends Actor {
    def act() {
        loop {
            react {
                case App("PinMan", session:Session) =>
                    new PinMan(session).start()
                case App("Konfirm", session: Session) =>
                    //new Konfirm(session).start()
                case App("PreKonfirm", session: Session) =>
                    //new PreKonfirm(session).start()
                case App("Record", session: Session) =>
                    //new Record(session).start()              
            }
        }
    }
}
