package com.fastagi.util

import com.fastagi.Messages
import com.fastagi.Session

object AgiUtils {
    def getData(fileName: String, app: AgiTrait): String = {
        app.rpc(AgiGetData(fileName, "", "")) match {
            case AgiResponse(result, data, endpoint) =>
                return result
        }
    }

    def getChannelVariable(varName: String, app: AgiTrait): String = {
        app.rpc(AgiGetChannelVariable(varName)) match {
            case AgiResponse(result, data, endpoint) =>
                result match {
                    case "0" => return "Null"
                    case "1" => return data
                }
        }
    }

    def playFile(fileName: String, app: AgiTrait): String = {
        app.rpc(AgiStreamFile(fileName, "", "")) match {
            case AgiResponse(result, data, endpoint) =>
                return result
        }
    }

    def getConfirmationStatus(fileName: String, doNext: Map[Int, String]) = {
    }
}

