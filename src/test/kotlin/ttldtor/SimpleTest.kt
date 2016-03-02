package ttldtor

import org.junit.Assert
import org.junit.Test
import java.util.*

class SimpleTest {
    fun Date.withZeroedTime(): Date {
        val calendar = Calendar.getInstance()

        calendar.time = this
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.time
    }

    @Test
    fun test1() {
        val parser = CjrChatLogParser()
        val result = parser.parse(Date().withZeroedTime(), "<html><body></body></html>")


        Assert.assertTrue(result.enterEvents.isEmpty())
        Assert.assertTrue(result.exitEvents.isEmpty())
        Assert.assertTrue(result.messageEvents.isEmpty())
        Assert.assertTrue(result.thirdPersonMessageEvent.isEmpty())
        Assert.assertTrue(result.allEvents.isEmpty())
    }

    @Test
    fun test2() {
        val parser = CjrChatLogParser()
        val result = parser.parse(Date().withZeroedTime(),
                """
                |<html>
                |   <body>
                |       <a name="20:15:42.123" href="#20:15:42.123" class="ts">[20:15:42]</a>
                |       <font class="ml">ckorzhik вышел из конференции</font><br/>
                |       <a name="20:37:16" href="#20:37:16" class="ts">[20:37:16]</a>
                |       <font class="mj">ħ зашёл в конференцию</font>
                |   </body>
                |</html>""".trimMargin())

        Assert.assertTrue(result.enterEvents.size == 1)
        Assert.assertTrue(result.exitEvents.size == 1)
        Assert.assertTrue(result.messageEvents.size == 0)
        Assert.assertTrue(result.thirdPersonMessageEvent.size == 0)
        Assert.assertTrue(result.allEvents.size == 2)
        Assert.assertTrue(result.enterEvents.first().who.equals("ħ"))
        Assert.assertTrue(result.exitEvents.first().who.equals("ckorzhik"))
    }
}
