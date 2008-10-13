package com.libraries

import java.util._
import java.io._

class Config(path: String) {
    val FILENAME_HELLO = "hello"
    val FILENAME_AUTH = "auth"
    val FILENAME_CONFIRM = "confirm"
    val FILENAME_GOODBYE_AUTH = "goodbye-auth"
    val FILENAME_GOODBYE_CONFIRM = "goodbye-confirm"
    val FILENAME_GOODBYE_CONFIRM_CONTACT = "goodbye-confirm-contact"
    val FILENAME_GOODBYE_CANCEL = "goodbye-cancel"
    val FILENAME_GOODBYE_CANCEL_CONTACT = "goodbye-cancel-contact"
    val FILETYPE_TEMPLATE = "template"
    val DEFAULT_PATH = "/etc/konfirm.properties"

    var dataHome: String = ""

    val props: Properties = new Properties()

    loadProperties()

    var ttwScale = ""
    var ttwFrequency = ""
    var ttwOType = ""
    var ttwFileExtension = ""

    setText2WaveProperties()

    def loadProperties(): Properties = {
        try {
            if(path == null) props.load(new FileInputStream(new File(DEFAULT_PATH)))
            else props.load(new FileInputStream(new File(path)))
            dataHome = props.getProperty("data-home")
        } catch {
            case e: Exception => e.printStackTrace()
        }
        props
    }

    def getFullPath(path: String): String = {
        val file: File = new File(dataHome, path)
        return file.getAbsolutePath()
    }

    def getFileName(fname: String): String = {
        props.getProperty(fname)
    }

    def get_path(dir: String, name: String): String = {
        val dirName = props.getProperty(dir)
        if(name == null) 
            return getFullPath(dirName)

        val fileName = props.getProperty(name)
        return getFullPath(new File(dirName, fileName).getAbsolutePath())
    }

    private def setText2WaveProperties() {
        ttwScale = props.getProperty("scale")
        ttwFrequency = props.getProperty("frequency")
        ttwOType = props.getProperty("output-type")
        ttwFileExtension = props.getProperty("file-extension")
    }

    def getProperty(propName: String): String = {
        return props.getProperty(propName)
    }
}
