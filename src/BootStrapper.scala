import javax.servlet._
import com.konfirm._

class BootStrapper extends ServletContextListener {


	val server = new Server(4573);

    override def contextInitialized(sce: ServletContextEvent) {
        println("initialising Context");
        //we need to run this code in a separate thread so that contextInitialised returns
        //else this method hangs/stops on line 16.
		try {
            new Thread() {
                override def run(): Unit = {
                    server.start();
                }
            }.start();
		} catch {
			case e: Exception => e.printStackTrace();
		}
        println("Context Succesfully Initialised")
	}

    override def contextDestroyed(sce: ServletContextEvent) {
        println("Stopping Server");
        server.stopListening()
        println("Server Stopped");
    }
}
