import com.fastagi._

object Run extends Application {
    val appServer = new AppServer()
    appServer.start()
    val acceptor = new Acceptor(2020, appServer)
    acceptor.start()
}
