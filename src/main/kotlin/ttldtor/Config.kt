package ttldtor

import java.util.*

object Config {
    val configFileName = "app.properties"
    val prop = Properties()

    init {
        prop.load(javaClass.classLoader.getResourceAsStream(configFileName))
    }

    val databaseUrl: String?
        get() = prop.getProperty("db")

    val user: String?
        get() = prop.getProperty("user")

    val password: String?
        get() = prop.getProperty("password")

    val locale: Locale
        get() {
            val l = prop.getProperty("locale")

            if (l.isNullOrEmpty()) {
                return Locale.getDefault()
            }

            val splitLocale = l.split("-")

            if (splitLocale.size == 1) {
                return Locale(splitLocale[0])
            }

            return Locale(splitLocale[0], splitLocale[1])
        }

    val processingDelay: Int
        get() {
            val d = prop.getProperty("processingDelay", "10")

            try {
                return Integer.parseInt(d)
            } catch (e: NumberFormatException)  {
                return 10
            }
        }
}