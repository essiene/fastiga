package com.konfirmagi.webservice

class URLMaker {
    
    val urls = Map(
        "account"->"http://localhost:5000/account/",
        "recorder"->"http://localhost:5000/recorder/",
        "cheque"->"http://localhost:5000/cheque/"
    )

    def url_for(controller: String, action: String, id: String, params: Map[String, String]): String = {

        var retVal = urls.get(controller).get().asInstanceOf[String]

        if(id != null)
            retVal = retVal + id + "/"

        if(action != null)
            retVal = retVal + action + "?"

        if(params != null) {                    
            params.foreach(param => 
                retVal = this.append(retVal, param)
            )
        }

        println(retVal)
        return retVal
    }

    def append(where: String, value: Tuple2[String, String]): String = {
        val retVal = where + value._1 + "=" + value._2 + "&"
        return retVal
    }

}
