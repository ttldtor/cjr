package ttldtor

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import java.util.*

class CjrChatLogParser: ChatLogParser {
    private companion object {
        final val timeStampClassName = "ts"
        final val joinEventClassName = "mj"
        final val leaveEventClassName = "ml"
        final val messageEventClassName = "mn"
        final val thirdPersonMessageClassName = "mne"
        final val joinOrLeaveMessageRegex = """^(.+) (?:зашёл|вышел|вышел|вошёл).+$""".toRegex()
        final val messageRegex = """^<(.+)> .*$""".toRegex(RegexOption.MULTILINE)
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

    private fun Element.collectText(): String {
        var text = this.textNodes().fold("") {
            a, b -> a + b.text()
        }

        if (this.hasClass(messageEventClassName)) {
            var next: Node? = this

            while (true) {
                next = next?.nextSibling()

                if (next == null) {
                    break
                }

                if (next is TextNode) {
                    text += next.text()
                } else if (next is Element) {
                    if (next.tagName() == "a") {
                        if (next.hasClass(timeStampClassName)) {
                            break
                        }

                        text += next.text()
                    } else if (next.tagName() == "br") {
                        text += "\n"
                    }
                }
            }
        }

        return text.trim()
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
        get() {
            val result = joinOrLeaveMessageRegex.find(this.collectText())?.groupValues?.get(1) ?: ""

            if (result.isEmpty()) {
                return messageRegex.find(this.collectText())?.groupValues?.get(1) ?: ""
            } else {
                return result
            }
        }

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
        val who = e.who
        val event = MessageEvent(timestamp = newMillis, who = who, message = e.collectText().replaceFirst("<$who> ", ""))

        return Pair(newMillis, event)
    }

    private fun parseThirdPersonMessageEvent(millis: Long, e: Element): Pair<Long, ThirdPersonMessageEvent>? {
        val newMillis = calculateMillis(millis, e) ?: return null
        val event = ThirdPersonMessageEvent(timestamp = newMillis, who = e.who, message = e.collectText())

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


        for (joinEventElement in joinEventsElements) {
            val parseResult = parseEnterEvent(millis, joinEventElement)

            if (parseResult != null) {
                allEvents.put(parseResult.first, parseResult.second)
                enterEventsList.add(parseResult.second)
            }
        }

        for (leaveEventElement in leaveEventsElements) {
            val parseResult = parseExitEvent(millis, leaveEventElement)

            if (parseResult != null) {
                allEvents.put(parseResult.first, parseResult.second)
                exitEventsList.add(parseResult.second)
            }
        }

        for (messageEventElement in messageEventsElements) {
            val parseResult = parseMessageEvent(millis, messageEventElement)

            if (parseResult != null) {
                allEvents.put(parseResult.first, parseResult.second)
                messageEventsList.add(parseResult.second)
            }
        }

        for (thirdPersonMessageEventElement in thirdPersonMessageEventsElements) {
            val parseResult = parseThirdPersonMessageEvent(millis, thirdPersonMessageEventElement)

            if (parseResult != null) {
                allEvents.put(parseResult.first, parseResult.second)
                thirdPersonMessageEventsList.add(parseResult.second)
            }
        }

        val nicknames: MutableSet<String> = mutableSetOf()

        for (event in allEvents) {
            val who = event.value.who

            if (who.isNullOrEmpty()) {
                continue
            }

            nicknames.add(who)
        }

        val nicknamesList = nicknames.sortedByDescending { it.length }

        for (thirdPersonMessage in thirdPersonMessageEventsList) {
            for (nick in nicknamesList) {
                if (thirdPersonMessage.message.startsWith(nick)) {
                    thirdPersonMessage.who = nick

                    break
                }
            }

            if (thirdPersonMessage.who.isEmpty()) {
                thirdPersonMessage.who = thirdPersonMessage.message.split(' ')[0]
            }

            thirdPersonMessage.message = thirdPersonMessage.message.replaceFirst("${thirdPersonMessage.who} ", "")
        }

        return ParseResult(enterEventsList, exitEventsList, messageEventsList, thirdPersonMessageEventsList, allEvents);
    }
}