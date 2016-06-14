package ttldtor.dao

import ttldtor.ConnectionPool
import ttldtor.poko.LogSite
import java.util.*
import ttldtor.use
import java.sql.Statement

object LogSiteDao {
    val CREATE_SQL = "INSERT INTO log_site (name, conference, url, last_parsed_timestamp) VALUES (?, ?, ?, ?)"
    val SAVE_SQL = "UPDATE log_site SET name = ?, conference = ?, url = ?, last_parsed_timestamp = ? WHERE id = ?"

    fun create(name: String, conference: String, url: String, lastParsedDate: Date): LogSite? {
        return ConnectionPool.connection.use { conn ->
            conn.prepareStatement(CREATE_SQL, Statement.RETURN_GENERATED_KEYS).use ust@ { st ->
                st.setString(1, name)
                st.setString(2, conference)
                st.setString(3, url)
                st.setLong(4, lastParsedDate.time)

                if (st.executeUpdate() == 0) {
                    return@ust null
                }

                st.generatedKeys.use ukeys@ {keys ->
                    if (keys.next()) {
                        return@ukeys LogSite(keys.getLong(1), name, conference, url, lastParsedDate)
                    }

                    null
                }
            }
        }
    }

    fun create(logSite: LogSite): LogSite? {
        return create(logSite.name, logSite.conference, logSite.url, logSite.lastParsedDate)
    }

    fun save(logSite: LogSite): Boolean {
        return ConnectionPool.connection.use { conn ->
            conn.prepareStatement(SAVE_SQL).use ust@ { st ->
                st.setString(1, logSite.name)
                st.setString(2, logSite.conference)
                st.setString(3, logSite.url)
                st.setLong(4, logSite.lastParsedDate.time)
                st.setLong(5, logSite.id)

                return@ust st.executeUpdate() == 1
            }
        }
    }
}