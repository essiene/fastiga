package com.fastagi

import scala.actors.Actor
import com.fastagi.apps.util._
import com.fastagi.util.AgiUtils

trait AgiTrait {   
    def rpc(agiRequest: AgiRequest): AgiResponse
    val jsonPipe = new JSONPipe()
    val urlMaker = new URLMaker()
    val agiUtils = new AgiUtils()
}
