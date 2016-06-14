package ttldtor.collectors

import org.jsoup.Jsoup
import java.util.*

class ChatLogUrlsCollector {
    fun collectDateParts(url: String, datePartRegex: Regex): MutableMap<Int, String> {
        val conn = Jsoup.connect(url)

        try {
            val doc = conn.get()
            val anchorElements = doc.getElementsByTag("a")

            if (anchorElements.isEmpty()) {
                return mutableMapOf()
            }

            val result: MutableMap<Int, String> = mutableMapOf()

            for (anchorElement in anchorElements) {
                val href = anchorElement.attr("href")
                val datePart = datePartRegex.find(href)?.groupValues?.get(1) ?: ""

                if (datePart.isEmpty()) {
                    continue
                }

                try {
                    val datePartValue = datePart.toInt()
                    result.put(datePartValue, url + "/" + datePart)
                } catch (_: NumberFormatException) {
                }
            }

            return result
        } catch (_: Exception) {
            return mutableMapOf()
        }
    }

    fun collectYearsOrMonths(url: String): MutableMap<Int, String> {
        return collectDateParts(url, """(\d+)/""".toRegex())
    }

    fun collectDays(url: String): MutableMap<Int, String> {
        return collectDateParts(url, """(\d+)\.html""".toRegex())
    }

    fun collect(url: String): ChatLogUrlsCollectionResult {
        val result = ChatLogUrlsCollectionResult()
        val yearToUrl = collectYearsOrMonths(url)

        result.years.addAll(yearToUrl.keys)

        for (year in result.years) {
            val yearUrl = yearToUrl[year]!!
            val monthToUrl = collectYearsOrMonths(yearUrl)

            result.yearToMonths.put(year, ArrayList<Int>(monthToUrl.keys))

            for (month in monthToUrl.keys) {
                val monthUrl = monthToUrl[month]!!
                val dayToUrl = collectDays(monthUrl)

                result.monthToDays.put(month, ArrayList<Int>(dayToUrl.keys))

                for (day in dayToUrl.keys) {
                    val dayUrl = dayToUrl[day]!! + ".html"
                    val calendar = Calendar.getInstance()

                    calendar.set(year, month - 1, day)
                    result.dateToUrl.put(calendar.time, dayUrl)
                }
            }
        }

        return result
    }
}