package ttldtor

import java.util.*

interface  ChatLogParser {
    fun parse(date: Date, fileContents: String): ParseResult
}