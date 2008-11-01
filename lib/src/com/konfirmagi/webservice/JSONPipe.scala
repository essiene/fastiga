package com.konfirmagi.webservice

import java.io._
import java.net._
import scala.util.parsing.json._
import java.util.Hashtable

class JSONPipe {


    var jsonAsMap = new Hashtable[String, String]()

    def connect(uri: String): String = {
        try {
            val reader = new BufferedReader(new InputStreamReader(new URL(uri).openStream()))
            val line = reader.readLine()    

            val json = JSON.parseFull(line)
            val jsonAsList = json.getOrElse(0).asInstanceOf[List[Any]]

            jsonAsList.foreach(this.map)
            println(this.jsonAsMap)        
            return "CONNECTED"
        } catch {
            case e: Exception =>
                return "CONNECTION LOST"
        }
    }

    def map(args: Any) = {       
        val tuple = args.asInstanceOf[Tuple2[String, String]]
        this.jsonAsMap.put(tuple._1, tuple._2)
    }

    def get(key: String): String = {
        val value = this.jsonAsMap.get(key)
        if(value == null)
            return ""
        else
            return value
    }
}
