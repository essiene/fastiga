import com.fastagi._

object Run extends Application {
    var acceptor = new Acceptor(2020)
    acceptor.start()
}
