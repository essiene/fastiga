package com.libraries

import com.konfirm._
import java.io._

class KonfirmAgi(service: ServiceClient, config: Config) {
    
    var request: String = ""

    def getVariable(varName: String): String = {
        request = "GET VARIABLE " + varName + "\n"
        service.sendRequest(request)
        val rawResp = service.getRawResponse()
        println(rawResp)
        service.getParsedResponse(rawResp)
    }
    
    def playFile(fName: String, path: String): String = {
        val fPath = getFullPath(fName, path)

        request = "EXEC Playback " + fPath.trim() + "\n"
        service.sendRequest(request)
        return service.getRawResponse()
    }

    def beep(): String = {
        request = "EXEC Playback beep"
        service.sendRequest(request)
        return service.getRawResponse()
    }

    def playCachedFile(callID: String): String = {
        return playFile(callID, config.getProperty("cache"))
    }

    def getSingleDigitNoHashKey(fName: String, path: String): String = {
        val fPath = getFullPath(fName, path)

        request = "GET DATA " + fPath + "20 1\n"
        service.sendRequest(request)
        return service.getParsedResponse(service.getRawResponse())
    }

    def getSingleDigitHashKey(fName: String, path: String): String = {
        val fPath = getFullPath(fName, path)
        var digit = ""
        request = "GET DATA " + fPath + "20 1\n"       
        service.sendRequest(request)
        var response = service.getParsedResponse(service.getRawResponse())
        while(response != null) {
            digit = response
            response = getSingleDigit(-1)
        }
        return digit
    }

    def getSingleDigit(waitTime: Int): String = {
        request = "GET DATA silence/10 " + String.valueOf(waitTime) + " 1\n"
        service.sendRequest(request)

        return service.getParsedResponse(service.getRawResponse())
    }
    
    def getFullPath(fName: String, path: String): String = {
        if(path == null) {
            return new File(config.getProperty("speech-out"), fName).getAbsolutePath()
        } else {
            return new File(path, fName).getAbsolutePath()
        }
    }

    def getMultipleDigits(): String = {
        var digits = ""
        var response = ""
        while(response != null) {
            response = getSingleDigit(-1)
            if(response != null) digits = digits + response
        }
        return digits.trim()
    }
}
