package ttldtor

import org.h2.jdbcx.JdbcConnectionPool
import java.sql.Connection

object ConnectionPool {
    val pool = JdbcConnectionPool.create(Config.databaseUrl, Config.user, Config.password)

    val connection: Connection
        get() = pool.connection
}