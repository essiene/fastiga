package com.libraries

import java.util.Hashtable
import com.konfirm._

class VoiceMenu(agi: KonfirmAgi, service: ServiceClient, fileToPlay: String, hangupTime: String, retryTime: Int, options: Hashtable[Int, String]) {

    def playAndGetResponse(): String = {
        var timer = 1;
        var request = "SET AUTO HANGUP " + hangupTime + "\n"
        service.sendRequest(request)
        service.getRawResponse()
        
        var response = ""
        while(timer < retryTime) {
            response = agi.getSingleDigitHashKey(fileToPlay, null)
            if(options.containsKey(response)) {
                request = "SET AUTO HANGUP 0\n"
                service.sendRequest(request)
                service.getRawResponse()
                return options.get(response)
            }
            timer = timer + 1
        }
        request = "SET AUTO HANGUP 0\n"
        service.sendRequest(request)
        service.getRawResponse()
        return null
    }
}
