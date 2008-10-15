package com.fastagi

import scala.actors.Actor
import scala.actors.Actor._

class AppServer extends Actor {
    def act() {
        loop {
            react {
                case Request("New", "PinMan", session:Session) =>
                    new PinMan(session).start()
                case Request("New", "Konfirm", session: Session) =>
                    //new Konfirm(session).start()
                case Request("New", "PreKonfirm", session: Session) =>
                    //new PreKonfirm(session).start()
                case Request("New", "Record", session: Session) =>
                    //new Record(session).start()              
            }
        }
    }
}
