import com.konfirmagi.parser._

class TestParser extends TestCase {
    def test1() = {
        parser = new Parser(new StringReader("200 result=0"))
        try {
            parser.parseOneLine()
        } catch {
            case e: Exception => e.printStackTrace()
        }
    }
}
