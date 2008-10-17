import javax.servlet._
import org.apache.log4j._
import com.fastagi._

class BootStrapper extends ServletContextListener {

/*    PropertyConfigurator.configure("/usr/share/tomcat5/common/classes/log4j.properties")
    val log: Logger = Logger.getLogger(this.getClass().getName())
    log.debug("Log4j Successfully Initialised in BootStrapper")

    val appServer = new AppServer()
	val server = new Acceptor(4573, appServer);
    log.debug("Server Object gotten Successsfully");
    */

    override def contextInitialized(sce: ServletContextEvent) {
     /*   server.start()
        appServer.start()
        log.debug("Server Started Successfully. Listening on port 4573")*/
	}

    override def contextDestroyed(sce: ServletContextEvent) {        
        //server.stop()
    }
}
