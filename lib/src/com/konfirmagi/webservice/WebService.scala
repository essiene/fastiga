package com.konfirmagi.webservice

import java.io.File

class WebService() {

    def setPreConfirmationStatus(accountID: String, chequeNumber: String, confirmationStatus: String, amount: String): boolean = {
        val urlMaker = new URLMaker()
        val jsonPipe = new JSONPipe()

        val url = urlMaker.url_for("cheque", "prekonfirm", chequeNumber, 
                                    Map("status"->confirmationStatus, "accountid"->accountID, "amount"->amount))
        var retVal = jsonPipe.connect(url)

        return getStatus("Status", retVal, jsonPipe)
    }

    def setConfirmationStatus(accountID: String, chequeNumber: String, confirmationStatus: String, transactionID: String): boolean = {
        val urlMaker = new URLMaker()
        val jsonPipe = new JSONPipe()

        val url = urlMaker.url_for("cheque", "konfirm", chequeNumber, 
                                    Map("status"->confirmationStatus, "accountid"->accountID, "transactionid"->transactionID))
        var retVal = jsonPipe.connect(url)

        return getStatus("Status", retVal, jsonPipe)
    }

    def getFullPath(fileName: String, appname: String, path: String): String = {
        val urlMaker = new URLMaker()
        val jsonPipe = new JSONPipe()

        val url = urlMaker.url_for("speech", "get_fullpath", null, Map("filename" -> fileName, "appname" -> appname, "path" -> path))
        val retVal = jsonPipe.connect(url)

        if(retVal.equals("CONNECTED")) {
            val status = jsonPipe.get("Status")
            if(status.equals("OK")) {
                return jsonPipe.get("fullpath")
            } else {
                return ""
            }
        } else {
            return ""
        }
    }

    def getConfirmPath(confirmationStatus: String, appname: String, path: String): String = {
        val urlMaker = new URLMaker()
        val jsonPipe = new JSONPipe()

        val url = urlMaker.url_for("speech", "get_confirmpath", null, Map("status" -> confirmationStatus, "appname" -> appname, "path" -> path))
        val retVal = jsonPipe.connect(url)

        if(retVal.equals("CONNECTED")) {
            val status = jsonPipe.get("Status")
            if(status.equals("OK")) {
                return jsonPipe.get("confirmpath")
            } else {
                return ""
            }
        } else {
            return ""
        }
    }

    def getFileName(recorderID: String): String = {
        val urlMaker = new URLMaker()
        val jsonPipe = new JSONPipe()

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

    def getAppName(recorderID: String): String = {
        val urlMaker = new URLMaker()
        val jsonPipe = new JSONPipe()

        var url = urlMaker.url_for("recorder", "get_appname", recorderID, null)

        val retVal = jsonPipe.connect(url)
        
        if(retVal.equals("CONNECTED")) {
            val status = jsonPipe.get("Status")
            if(status.equals("OK")) {
                return jsonPipe.get("appname")
            } else {
                return ""
            }
        } else {
            return ""
        }
    }

    def saveRecordedFile(recorderID: String, fileName: String, fullPath: String, appName: String): boolean = {
        val urlMaker = new URLMaker()
        val jsonPipe = new JSONPipe()

        val src = fullPath
        val dest = fileName

        println("SRC: " + src)
        println("DEST: " + dest)

        val url = urlMaker.url_for("recorder", "write", recorderID, Map("from"->src, "to"->dest, "appname"->appName))

        val retVal = jsonPipe.connect(url)
        
        return getStatus("Status", retVal, jsonPipe)
    }

    def isValid(accountNumber: String, pin: String): boolean = {
        val urlMaker = new URLMaker()
        val jsonPipe = new JSONPipe()

        val url = urlMaker.url_for("account","isvalid",accountNumber, Map[String, String]("pin"->pin))
        println(url)

        val retVal = jsonPipe.connect(url)

        return getStatus("Status", retVal, jsonPipe)
        
    }

    def changePin(accountNumber: String, newPin: String): boolean = {
        val urlMaker = new URLMaker()
        val jsonPipe = new JSONPipe()

        val url = urlMaker.url_for("account", "change_pin", accountNumber, Map[String, String]("newpin"->newPin))

        val retVal = jsonPipe.connect(url)
        
        return getStatus("Status", retVal, jsonPipe)
    }

    def getStatus(status: String, retVal: String, jsonPipe: JSONPipe): boolean = {
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
