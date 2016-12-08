package ttldtor

import org.junit.Assert
import org.junit.Test
import ttldtor.parsers.CjrChatLogParser
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
        Assert.assertTrue(result.thirdPersonMessageEvents.isEmpty())
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
        Assert.assertTrue(result.messageEvents.isEmpty())
        Assert.assertTrue(result.thirdPersonMessageEvents.isEmpty())
        Assert.assertTrue(result.allEvents.size == 2)
        Assert.assertTrue(result.enterEvents.first().who == "ħ")
        Assert.assertTrue(result.exitEvents.first().who == "ckorzhik")
    }

    @Test
    fun test3() {
        val parser = CjrChatLogParser()
        val result = parser.parse(Date().withZeroedTime(),
                """
                |<html>
                |   <body>
                |       <a name="20:15:42.123" href="#20:15:42.123" class="ts">[20:15:42]</a>
                |       <font class="ml">uggi 345 вышел(а) из комнаты</font><br/>
                |       <a name="20:37:16" href="#20:37:16" class="ts">[20:37:16]</a>
                |       <font class="mj">ħ 123 зашёл в конференцию</font>
                |   </body>
                |</html>""".trimMargin())

        Assert.assertTrue(result.enterEvents.size == 1)
        Assert.assertTrue(result.exitEvents.size == 1)
        Assert.assertTrue(result.messageEvents.isEmpty())
        Assert.assertTrue(result.thirdPersonMessageEvents.isEmpty())
        Assert.assertTrue(result.allEvents.size == 2)
        Assert.assertTrue(result.enterEvents.first().who == "ħ 123")
        Assert.assertTrue(result.exitEvents.first().who == "uggi 345")
    }

    @Test
    fun test4() {
        val parser = CjrChatLogParser()
        val result = parser.parse(Date().withZeroedTime(),
                """
                |<html>
                |   <body>
                |       <a name="abcd" href="#20:15:42.123" class="ts">[20:15:42]</a>
                |       <font class="ml">uggi 345 вышел(а) из комнаты</font><br/>
                |       <a name="123213123" href="#20:37:16" class="ts">[20:37:16]</a>
                |       <font class="mj">ħ 123 зашёл в конференцию</font>
                |   </body>
                |</html>""".trimMargin())

        Assert.assertTrue(result.enterEvents.isEmpty())
        Assert.assertTrue(result.exitEvents.isEmpty())
        Assert.assertTrue(result.messageEvents.isEmpty())
        Assert.assertTrue(result.thirdPersonMessageEvents.isEmpty())
        Assert.assertTrue(result.allEvents.isEmpty())
    }

    @Test
    fun test5() {
        val parser = CjrChatLogParser()
        val result = parser.parse(Date().withZeroedTime(),
                """
                |<html>
                |   <body>
                |       <a name="abcd" href="#20:15:42.123" class="ts">[20:15:42]</a>
                |       <font class="ml">uggi 345 вышел(а) из комнаты</font><br/>
                |       <a name="123213123" href="#20:37:16" class="ts">[20:37:16]</a>
                |       <font class="mj">ħ 123 зашёл в конференцию</font>
                |       <a id="11:14:54.394156" name="11:14:54.394156" href="#11:14:54.394156" class="ts">[11:14:54]</a>
                |       <font class="mn">&lt;ħ&gt;</font> где мантикор?<br>
                |   </body>
                |</html>""".trimMargin())

        Assert.assertTrue(result.enterEvents.isEmpty())
        Assert.assertTrue(result.exitEvents.isEmpty())
        Assert.assertTrue(result.messageEvents.size == 1)
        Assert.assertTrue(result.messageEvents[0].who == "ħ")
        Assert.assertTrue(result.messageEvents[0].message == "где мантикор?")
        Assert.assertTrue(result.thirdPersonMessageEvents.isEmpty())
        Assert.assertTrue(result.allEvents.size == 1)
    }

    @Test
    fun test6() {
        val parser = CjrChatLogParser()
        val result = parser.parse(Date().withZeroedTime(),
                """
                |<html>
                |   <body>
                |       <a id="00:08:29.482382" name="00:08:29.482382" href="#00:08:29.482382" class="ts">[00:08:29]</a>
                |       <font class="mn">&lt;gr_buza@arbeiten&gt;</font> 153-34-168-192:~ eill$ host draw.io<br/>draw.io has address 216.239.38.21<br/>draw.io has address 216.239.34.21<br/>draw.io has address 216.239.36.21<br/>draw.io has address 216.239.32.21<br/>draw.io has IPv6 address 2001:4860:4802:38::15<br/>draw.io has IPv6 address 2001:4860:4802:32::15<br/>draw.io has IPv6 address 2001:4860:4802:34::15<br/>draw.io has IPv6 address 2001:4860:4802:36::15<br/>draw.io mail is handled by 5 alt1.aspmx.l.google.com.<br/>draw.io mail is handled by 10 aspmx2.googlemail.com.<br/>draw.io mail is handled by 5 alt2.aspmx.l.google.com.<br/>draw.io mail is handled by 1 aspmx.l.google.com.<br/>
                |       <a id="00:08:33.384387" name="00:08:33.384387" href="#00:08:33.384387" class="ts">[00:08:33]</a>
                |       <font class="mn">&lt;gr_buza@arbeiten&gt;</font> держи <a href="http://lenta.ru/news/2016/03/25/baldyandex/" rel="nofollow">http://lenta.ru/news/2016/03/25/baldyandex/</a><br/>
                |   </body>
                |</html>""".trimMargin())

        Assert.assertTrue(result.enterEvents.isEmpty())
        Assert.assertTrue(result.exitEvents.isEmpty())
        Assert.assertTrue(result.messageEvents.size == 2)
        Assert.assertTrue(result.messageEvents[0].who == "gr_buza@arbeiten")
        Assert.assertTrue(result.messageEvents[0].message == "153-34-168-192:~ eill$ host draw.io\ndraw.io has address 216.239.38.21\ndraw.io has address 216.239.34.21\ndraw.io has address 216.239.36.21\ndraw.io has address 216.239.32.21\ndraw.io has IPv6 address 2001:4860:4802:38::15\ndraw.io has IPv6 address 2001:4860:4802:32::15\ndraw.io has IPv6 address 2001:4860:4802:34::15\ndraw.io has IPv6 address 2001:4860:4802:36::15\ndraw.io mail is handled by 5 alt1.aspmx.l.google.com.\ndraw.io mail is handled by 10 aspmx2.googlemail.com.\ndraw.io mail is handled by 5 alt2.aspmx.l.google.com.\ndraw.io mail is handled by 1 aspmx.l.google.com.")
        Assert.assertTrue(result.messageEvents[1].who == "gr_buza@arbeiten")
        Assert.assertTrue(result.messageEvents[1].message == "держи http://lenta.ru/news/2016/03/25/baldyandex/")
        Assert.assertTrue(result.thirdPersonMessageEvents.isEmpty())
    }

    @Test
    fun test7() {
        val parser = CjrChatLogParser()
        val result = parser.parse(Date().withZeroedTime(),
                """
                |<html>
                |   <body>
                |       <a id="21:17:49.826752" name="21:17:49.826752" href="#21:17:49.826752" class="ts">[21:17:49]</a>
                |       <font class="mne">ermine покорячила говядину - чем свежее говядина, тем существующий код менее рабочий</font><br/>
                |   </body>
                |</html>""".trimMargin())

        Assert.assertTrue(result.enterEvents.isEmpty())
        Assert.assertTrue(result.exitEvents.isEmpty())
        Assert.assertTrue(result.messageEvents.isEmpty())
        Assert.assertTrue(result.thirdPersonMessageEvents.size == 1)
        Assert.assertTrue(result.thirdPersonMessageEvents[0].who == "ermine")
        Assert.assertTrue(result.thirdPersonMessageEvents[0].message == "покорячила говядину - чем свежее говядина, тем существующий код менее рабочий")
    }

    @Test
    fun test8() {
        val parser = CjrChatLogParser()
        val result = parser.parse(Date().withZeroedTime(),
                """
                |<html>
                |   <body>
                |       <a name="20:15:42.123" href="#20:15:42.123" class="ts">[20:15:42]</a>
                |       <font class="mj">ħ 123 зашёл в конференцию</font>
                |       <a name="20:37:16" href="#20:37:16" class="ts">[20:37:16]</a>
                |       <font class="mj">ħ зашёл в конференцию</font>
                |       <a id="21:17:49.826752" name="21:17:49.826752" href="#21:17:49.826752" class="ts">[21:17:49]</a>
                |       <font class="mne">ħ 123 покорячил говядину - чем свежее говядина, тем существующий код менее рабочий</font><br/>
                |   </body>
                |</html>""".trimMargin())

        Assert.assertTrue(result.enterEvents.size == 2)
        Assert.assertTrue(result.exitEvents.isEmpty())
        Assert.assertTrue(result.messageEvents.isEmpty())
        Assert.assertTrue(result.thirdPersonMessageEvents.size == 1)
        Assert.assertTrue(result.thirdPersonMessageEvents[0].who == "ħ 123")
        Assert.assertTrue(result.thirdPersonMessageEvents[0].message == "покорячил говядину - чем свежее говядина, тем существующий код менее рабочий")
    }
}
