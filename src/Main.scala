import java.io._
import javax.servlet._
import javax.servlet.http._

class Main extends HttpServlet
{
    override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
        response.setContentType("text/plain")
        val out = response.getWriter
        out.println("Hello Weaver servlet world!")
    }
}
