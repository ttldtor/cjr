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

    fun parseEnterEvent(millis: Long, e: Element): Pair<Long, EnterEvent>? {
        val timeStamp = e.getTimeStampElement()

        if (timeStamp != null) {
            val newMillis = millis + timeStamp.millis
            val event = EnterEvent(timestamp = newMillis, who = e.who)

            return Pair(newMillis, event)
        }

        return null
    }

    fun parseExitEvent(millis: Long, e: Element): Pair<Long, ExitEvent>? {
        val timeStamp = e.getTimeStampElement()

        if (timeStamp != null) {
            val newMillis = millis + timeStamp.millis
            val event = ExitEvent(timestamp = newMillis, who = e.who)

            return Pair(newMillis, event)
        }

        return null
    }

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
            val parseResult = parseEnterEvent(millis, j)

            if (parseResult != null) {
                allEvents.put(parseResult.first, parseResult.second)
                enterEventsList.add(parseResult.second)
            }
        }

        for (l in leaveEvents) {
            val parseResult = parseExitEvent(millis, l)

            if (parseResult != null) {
                allEvents.put(parseResult.first, parseResult.second)
                exitEventsList.add(parseResult.second)
            }
        }

        return ParseResult(enterEventsList, exitEventsList, messageEventsList, thirdPersonMessageEventsList, allEvents);
    }
}