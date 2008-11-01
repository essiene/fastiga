package com.fastagi

import scala.actors.Actor
import com.fastagi.util.PropertyFile
import com.konfirmagi.webservice.WebService

trait AgiTrait {   
    def rpc(agiRequest: AgiRequest): AgiResponse    
    val prop = PropertyFile.loadProperties("/etc/fastagi/agi.properties")
    val webService = new WebService()
}
