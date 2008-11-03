package com.konfirmagi.webservice

class URLMaker {
    val prop = PropertyFile.loadProperties("/etc/fastagi/webservice.properties")
    val base_url = PropertyFile.getProperty(prop, "base.url", "http://localhost:5000/")
    val account_url = PropertyFile.getProperty(prop, "account.url", "http://localhost:5000/account/")
    val recorder_url = PropertyFile.getProperty(prop, "recorder.url", "http://localhost:5000/recorder/")
    val cheque_url = PropertyFile.getProperty(prop, "cheque.url", "http://localhost:5000/cheque")


    val urls = Map(
        "account"->account_url,
        "recorder"->recorder_url,
        "cheque"->cheque_url
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
