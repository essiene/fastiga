package com.konfirmagi.webservice

class WebService() {
    
    val urlMaker = new URLMaker()
    val jsonPipe = new JSONPipe()

    def setPreConfirmationStatus(accountID: String, chequeNumber: String, confirmationStatus: String, amount: String): boolean = {
        val url = urlMaker.url_for("cheque", "prekonfirm", chequeNumber, 
                                    Map("status"->confirmationStatus, "accountid"->accountID, "amount"->amount))
        var retVal = jsonPipe.connect(url)

        return getStatus("Status", retVal)
    }

    def setConfirmationStatus(accountID: String, chequeNumber: String, confirmationStatus: String, transactionID: String): boolean = {
        val url = urlMaker.url_for("cheque", "prekonfirm", chequeNumber, 
                                    Map("status"->confirmationStatus, "accountid"->accountID, "transactionid"->transactionID))
        var retVal = jsonPipe.connect(url)

        return getStatus("Status", retVal)
    }

    def getFileName(recorderID: String): String = {
        var url = urlMaker.url_for("recorder", "get_filename", recorderID, null)

        val retVal = jsonPipe.connect(url)
        
        if(retVal.equals("CONNECTED")) {
            val status = jsonPipe.get("Status")
            if(status.equals("OK")) {
                return jsonPipe.get("filename")
            } else {
                return ""
            }
        } else {
            return ""
        }
    }

    def saveRecordedFile(recorderID: String, fileName: String, fullPath: String, speechPath: String): boolean = {
        val src = fullPath + ".ulaw"
        val dest = speechPath + fileName + ".ulaw"

        val url = urlMaker.url_for("recorder", "write", recorderID, Map("from"->src, "to"->dest))

        val retVal = jsonPipe.connect(url)
        
        return getStatus("Status", retVal)
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
}
