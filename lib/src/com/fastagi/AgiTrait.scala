package com.fastagi

import scala.actors.Actor
import scala.actors.Actor._

trait AgiTrait {   
    def remoteCall(session: Session, request: AgiRequest): AgiResponse = {
        session ! request
        receive {
            case agiResponse: AgiResponse => 
                return agiResponse
            case _  => 
                return null                
        }
    }
}
