import java.io._
import javax.servlet._
import javax.servlet.http._

import scala.actors.Actor._

import com.fastagi._

class Main extends HttpServlet {
    override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
        response.setContentType("text/plain")
    }    
}
