package ttldtor

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.*

class CjrChatLogParser: ChatLogParser {
    private companion object {
        final val timeStampClassName = "ts"
        final val joinEventClassName = "mj"
        final val leaveEventClassName = "ml"
        final val messageEventClassName = "mn"
        final val thirdPersonMessageClassName = "mne"
        final val joinOrLeaveMessageRegex = """^(.+) (?:зашёл|вышел|вышел|вошёл).+$""".toRegex()
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

    private val Element.millis: Long?
        get() {
            val timeElements = this.attr("name").split(':')

            if (timeElements.size != 3) {
                return null
            }

            val secondsParts = timeElements[2].split('.')

            try {
                val hours = timeElements[0].toLong()
                val minutes = timeElements[1].toLong()
                val seconds = secondsParts[0].toLong()
                val millis = if (secondsParts.size == 2) secondsParts[1].toLong() else 0

                println("${this.attr("name")} -> $hours:$minutes:$seconds.$millis")

                return hours * 3600000 + minutes * 60000 + seconds * 1000 + millis
            } catch(_:NumberFormatException) {
                return null
            }
        }

    private val Element.who: String
        get() = joinOrLeaveMessageRegex.find(this.collectedText)?.groupValues?.get(1) ?: ""

    private fun calculateMillis(millis: Long, e: Element): Long? {
        val timeStampMillis = e.getTimeStampElement()?.millis ?: return null

        return millis + timeStampMillis
    }

    private fun parseEnterEvent(millis: Long, e: Element): Pair<Long, EnterEvent>? {
        val newMillis = calculateMillis(millis, e) ?: return null
        val event = EnterEvent(timestamp = newMillis, who = e.who)

        return Pair(newMillis, event)
    }

    private fun parseExitEvent(millis: Long, e: Element): Pair<Long, ExitEvent>? {
        val newMillis = calculateMillis(millis, e) ?: return null
        val event = ExitEvent(timestamp = newMillis, who = e.who)

        return Pair(newMillis, event)
    }

    private fun parseMessageEvent(millis: Long, e: Element): Pair<Long, MessageEvent>? {
        val newMillis = calculateMillis(millis, e) ?: return null
        val event = MessageEvent(timestamp = newMillis, who = e.who, message = "")

        return Pair(newMillis, event)
    }

    private fun parseThirdPersonMessageEvent(millis: Long, e: Element): Pair<Long, ThirdPersonMessageEvent>? {
        val newMillis = calculateMillis(millis, e) ?: return null
        val event = ThirdPersonMessageEvent(timestamp = newMillis, who = e.who, message = "")

        return Pair(newMillis, event)
    }


    override fun parse(date: Date, fileContents: String): ParseResult {
        val doc = Jsoup.parse(fileContents)
        val millis = date.time

        val joinEventsElements = doc.getElementsByClass(joinEventClassName)
        val leaveEventsElements = doc.getElementsByClass(leaveEventClassName)
        val messageEventsElements = doc.getElementsByClass(messageEventClassName)
        val thirdPersonMessageEventsElements = doc.getElementsByClass(thirdPersonMessageClassName)

        val allEvents: MutableMap<Long, IEvent> = mutableMapOf()
        val enterEventsList: MutableList<EnterEvent> = mutableListOf()
        val exitEventsList: MutableList<ExitEvent> = mutableListOf()
        val messageEventsList: MutableList<MessageEvent> = mutableListOf()
        val thirdPersonMessageEventsList: MutableList<ThirdPersonMessageEvent> = mutableListOf()


        for (j in joinEventsElements) {
            val parseResult = parseEnterEvent(millis, j)

            if (parseResult != null) {
                allEvents.put(parseResult.first, parseResult.second)
                enterEventsList.add(parseResult.second)
            }
        }

        for (l in leaveEventsElements) {
            val parseResult = parseExitEvent(millis, l)

            if (parseResult != null) {
                allEvents.put(parseResult.first, parseResult.second)
                exitEventsList.add(parseResult.second)
            }
        }

        for (m in messageEventsElements) {
            val parseResult = parseMessageEvent(millis, m)

            if (parseResult != null) {
                allEvents.put(parseResult.first, parseResult.second)
                messageEventsList.add(parseResult.second)
            }
        }

        for (tp in thirdPersonMessageEventsElements) {
            val parseResult = parseThirdPersonMessageEvent(millis, tp)

            if (parseResult != null) {
                allEvents.put(parseResult.first, parseResult.second)
                thirdPersonMessageEventsList.add(parseResult.second)
            }
        }

        return ParseResult(enterEventsList, exitEventsList, messageEventsList, thirdPersonMessageEventsList, allEvents);
    }
}