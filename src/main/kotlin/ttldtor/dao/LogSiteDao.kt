package ttldtor.dao

import ttldtor.ConnectionPool
import ttldtor.poko.LogSite
import java.util.*
import ttldtor.use
import java.sql.Statement

object LogSiteDao {
    val CREATE_SQL = "INSERT INTO log_site (name, conference, url, last_parsed_timestamp) VALUES (?, ?, ?, ?)"
    val SAVE_SQL = "UPDATE log_site SET name = ?, conference = ?, url = ?, last_parsed_timestamp = ? WHERE id = ?"
    val DELETE_SQL = "UPDATE log_site SET deleted = TRUE WHERE id = ?"
    val GET_BY_ID_SQL = "SELECT * FROM log_site WHERE id = ? AND (deleted IS NULL OR (deleted IS NOT NULL AND deleted = FALSE))"
    val GET_SQL = "SELECT * FROM log_site WHERE deleted IS NULL OR (deleted IS NOT NULL AND deleted = FALSE)"

    fun create(name: String, conference: String, url: String, lastParsedDate: Date): LogSite? {
        return ConnectionPool.connection.use { conn ->
            conn.prepareStatement(CREATE_SQL, Statement.RETURN_GENERATED_KEYS).use useSt@ { st ->
                st.setString(1, name)
                st.setString(2, conference)
                st.setString(3, url)
                st.setLong(4, lastParsedDate.time)

                if (st.executeUpdate() == 0) {
                    return@useSt null
                }

                st.generatedKeys.use useKeys@ {keys ->
                    if (keys.next()) {
                        return@useKeys LogSite(
                                id = keys.getLong(1),
                                name = name,
                                conference = conference,
                                url = url,
                                lastParsedDate = lastParsedDate,
                                deleted = false)
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
            conn.prepareStatement(SAVE_SQL).use { st ->
                st.setString(1, logSite.name)
                st.setString(2, logSite.conference)
                st.setString(3, logSite.url)
                st.setLong(4, logSite.lastParsedDate.time)
                st.setLong(5, logSite.id)

                st.executeUpdate() == 1
            }
        }
    }

    fun delete(logSite: LogSite): Boolean {
        return ConnectionPool.connection.use { conn ->
            conn.prepareStatement(DELETE_SQL).use { st ->
                st.setLong(1, logSite.id)

                st.executeUpdate() == 1
            }
        }
    }

    fun getById(id: Long): LogSite? {
        return ConnectionPool.connection.use { conn ->
            conn.prepareStatement(GET_BY_ID_SQL).use { st ->
                st.setLong(1, id)

                st.executeQuery().use useResultSet@ { resultSet ->
                    if (resultSet.next()) {
                        return@useResultSet LogSite(
                                id = resultSet.getLong(1),
                                name = resultSet.getString(2),
                                conference = resultSet.getString(3),
                                url = resultSet.getString(4),
                                lastParsedDate = Date(resultSet.getLong(5)),
                                deleted = false)
                    }

                    null
                }
            }
        }
    }

    fun get(): List<LogSite> {
        return ConnectionPool.connection.use { conn ->
            conn.prepareStatement(GET_SQL).use { st ->
                st.executeQuery().use { resultSet ->
                    val result = arrayListOf<LogSite>()

                    while (resultSet.next()) {
                        result.add(LogSite(
                                id = resultSet.getLong(1),
                                name = resultSet.getString(2),
                                conference = resultSet.getString(3),
                                url = resultSet.getString(4),
                                lastParsedDate = Date(resultSet.getLong(5)),
                                deleted = false))
                    }

                    result
                }
            }
        }
    }
}