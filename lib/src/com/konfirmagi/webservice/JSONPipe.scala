package com.konfirmagi.webservice

import java.io._
import java.net._
import scala.util.parsing.json._

class JSONPipe {

    var jsonAsList = List[Any]()

    def connect(uri: String): String = {
        try {
            val reader = new BufferedReader(new InputStreamReader(new URL(uri).openStream()))
            val line = reader.readLine()    

            val json = JSON.parseFull(line)
            this.jsonAsList = json.getOrElse(0).asInstanceOf[List[Any]]
            
            println(this.jsonAsList)
            return "CONNECTED"
        } catch {
            case e: Exception =>
                return "CONNECTION LOST"
        }
    }

    def get(key: String): String = {
        this.jsonAsList.foreach(args =>
            {
                val tuple = args.asInstanceOf[Tuple2[String, String]]
                println("TUPEL: " + tuple)
                key match {
                    case tuple._1 => return tuple._2
                    case _ => 
                }
            }
        )
        return null
    }
}
