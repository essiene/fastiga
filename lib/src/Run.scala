import com.fastagi._

object Run extends Application {
    val appServer = new AppServer("com.fastagi.apps")
    println("Server is Ready!")
    appServer.start()

    val acceptor = new Acceptor(2020, appServer)
    acceptor.start()
}
