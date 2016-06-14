package ttldtor.collectors

import java.util.*

class ChatLogUrlsCollectionResult {
    val years: MutableList<Int> = mutableListOf()
    val yearToMonths:MutableMap<Int, MutableList<Int>> = mutableMapOf()
    val monthToDays:MutableMap<Int, MutableList<Int>> = mutableMapOf()
    val dateToUrl:MutableMap<Date, String> = mutableMapOf()
}