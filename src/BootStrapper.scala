import javax.servlet._
import org.apache.log4j._
import com.fastagi._
import com.fastagi.util._
import scala.actors.Actor._

class BootStrapper extends ServletContextListener {

    PropertyConfigurator.configure("/usr/share/tomcat5/common/classes/log4j.properties");
    val log: Logger = Logger.getLogger(this.getClass().getName());
    log.debug("Log4j Successfully Initialised in BootStrapper");

    var acceptor: Acceptor = null

    override def contextInitialized(sce: ServletContextEvent) {
        val ivrPP = new IVRProperties(sce.getServletContext)
        val appPackage = ivrPP.getAppProperty("app.package")
        val port = Integer.parseInt(ivrPP.getAppProperty("app.server.port"))

        val appServer = new AppServer(appPackage)
        appServer.start()

        log.debug("Server is Ready!")
        
        acceptor = new Acceptor(port, appServer)
        acceptor.start()
	}

    override def contextDestroyed(sce: ServletContextEvent) {
        log.debug("Stopping Service");
        if(acceptor != null)
            acceptor.close()
    }
}
