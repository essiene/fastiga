import com.fastiga._
import java.util.Properties
import com.fastiga.util.PropertyFile

object Run extends Application {
    val props = PropertyFile.loadProperties("/etc/fastiga/app.properties")
    
    val appsPackage = PropertyFile.getProperty(props, "apps.package","com.fastiga.apps")
    val appServer = new AppServer(appsPackage)
    println("Server is Ready!")
    appServer.start()
    
    val port = Integer.parseInt(PropertyFile.getProperty(props, "app.server.port", "2020"))

    val acceptor = new Acceptor(port, appServer)
    acceptor.start()
}
