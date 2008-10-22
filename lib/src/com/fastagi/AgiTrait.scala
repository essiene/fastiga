package com.fastagi

import scala.actors.Actor

trait AgiTrait {   
    def rpc(agiRequest: AgiRequest): AgiResponse
}
