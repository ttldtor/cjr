package ttldtor

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import java.util.*

class CjrChatLogParser: ChatLogParser {
    companion object {
        final val timeStampClassName = "ts"
        final val joinEventClassName = "mj"
        final val leaveEventClassName = "ml"
        final val messageEventClassName = "mn"
        final val thirdPersonMessageClassName = "mne"
    }

    fun getTimeStampElement(e: Element): Element? {
        val prev = e.previousElementSibling()

        while (prev != null) {
            if (prev.hasClass(timeStampClassName)) {
                return prev;
            }

            if (prev.hasClass(joinEventClassName)
                    || prev.hasClass(leaveEventClassName)
                    || prev.hasClass(messageEventClassName)
                    || prev.hasClass(thirdPersonMessageClassName)) {
                return null
            }
        }

        return null
    }


    fun getMillis(e: Element): Long {
        val timeElements = e.attr("name").split(":.")

        return timeElements[0].toLong() * 3600000 + timeElements[1].toLong() * 60000 + timeElements[2].toLong() * 1000
    }

    override fun parse(date: Date, fileContents: String): Map<Long, IEvent> {
        val doc = Jsoup.parse(fileContents)
        val millis = date.time
        val joinEvents = doc.getElementsByClass(joinEventClassName)
        val result: MutableMap<Long, IEvent> = mutableMapOf()


        for (j in joinEvents) {
            val texts = j.textNodes()

            val text = texts.fold("") {
                a, b -> a + b.text()
            }

            val who = text.split(" ")[0]
            val timeStamp = getTimeStampElement(j)

            if (timeStamp != null) {
                val newMillis = millis + getMillis(timeStamp)

                result.put(newMillis, EnterEvent(timestamp = millis + getMillis(timeStamp), who = who))
            }
        }




        return result;
    }
}