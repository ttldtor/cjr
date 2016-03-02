package ttldtor

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import java.util.*
import kotlin.properties.Delegates

class CjrChatLogParser: ChatLogParser {
    private companion object {
        final val timeStampClassName = "ts"
        final val joinEventClassName = "mj"
        final val leaveEventClassName = "ml"
        final val messageEventClassName = "mn"
        final val thirdPersonMessageClassName = "mne"
    }

    private fun Element.getTimeStampElement(): Element? {
        val prev = this.previousElementSibling()

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

    private val Element.collectedText: String
        get() = this.textNodes().fold("") {
            a, b -> a + b.text()
        }

    private val Element.millis: Long
        get() {
            val timeElements = this.attr("name").split(':')
            val secondsParts = timeElements[2].split('.')

            try {
                val hours = timeElements[0].toLong()
                val minutes = timeElements[1].toLong()
                val seconds = secondsParts[0].toLong()
                val millis = if (secondsParts.size == 2) secondsParts[1].toLong() else 0

                println("${this.attr("name")} -> $hours:$minutes:$seconds.$millis")

                return hours * 3600000 + minutes * 60000 + seconds * 1000 + millis
            } catch(_:NumberFormatException) {
                return 0
            }
        }

    private val Element.who: String
        get() = this.collectedText.split(" ")[0]

    override fun parse(date: Date, fileContents: String): ParseResult {
        val doc = Jsoup.parse(fileContents)
        val millis = date.time
        val joinEvents = doc.getElementsByClass(joinEventClassName)
        val leaveEvents = doc.getElementsByClass(leaveEventClassName)
        val allEvents: MutableMap<Long, IEvent> = mutableMapOf()
        val enterEventsList: MutableList<EnterEvent> = mutableListOf()
        val exitEventsList: MutableList<ExitEvent> = mutableListOf()
        val messageEventsList: MutableList<MessageEvent> = mutableListOf()
        val thirdPersonMessageEventsList: MutableList<ThirdPersonMessageEvent> = mutableListOf()


        for (j in joinEvents) {
            val who = j.who
            val timeStamp = j.getTimeStampElement()

            if (timeStamp != null) {
                val newMillis = millis + timeStamp.millis
                val event = EnterEvent(timestamp = newMillis, who = who)

                allEvents.put(newMillis, event)
                enterEventsList.add(event)
            }
        }

        for (l in leaveEvents) {
            val who = l.who
            val timeStamp = l.getTimeStampElement()

            if (timeStamp != null) {
                val newMillis = millis + timeStamp.millis
                val event = ExitEvent(timestamp = newMillis, who = who)

                allEvents.put(newMillis, event)
                exitEventsList.add(event)
            }
        }

        return ParseResult(enterEventsList, exitEventsList, messageEventsList, thirdPersonMessageEventsList, allEvents);
    }
}