package com.konfirmagi.webservice

class WebService() {
    
    val urlMaker = new URLMaker()
    val jsonPipe = new JSONPipe()

    def getStatus(status: String, retVal: String): boolean = {
        if(retVal.equals("CONNECTED")) {
            val status = jsonPipe.get("Status")
            if(status.equals("OK")) 
                return true
            else
                return false
        } else {
            return false
        }
    }

    def isValid(accountNumber: String, pin: String): boolean = {
        val url = urlMaker.url_for("account","isvalid",accountNumber, Map[String, String]("pin"->pin))

        val retVal = jsonPipe.connect(url)

        return getStatus("Status", retVal)
        
    }

    def changePin(accountNumber: String, newPin: String): boolean = {
        val url = urlMaker.url_for("account", "change_pin", accountNumber, Map[String, String]("newpin"->newPin))

        val retVal = jsonPipe.connect(url)
        
        return getStatus("Status", retVal)
    }
}
