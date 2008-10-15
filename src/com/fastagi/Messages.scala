package com.fastagi

case class Messages(msg: Object)

case class Response(request: Object, response: Object)

case class Request(action: String, script: String, session: Session)
