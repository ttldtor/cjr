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
}