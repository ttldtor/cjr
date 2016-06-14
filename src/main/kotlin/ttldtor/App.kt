package ttldtor

import javafx.application.Application;
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.stage.Stage
import ttldtor.ui.javafx.models.LogSiteModel
import ttldtor.ui.javafx.tables.LogSiteTable
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.FlywayException
import org.h2.tools.Script
import ttldtor.dao.LogSiteDao
import ttldtor.ui.javafx.runAsync
import ttldtor.ui.javafx.ui
import java.sql.SQLException
import java.util.*
import org.slf4j.LoggerFactory;
import ttldtor.collectors.ChatLogUrlsCollector
import ttldtor.ui.AddLogSiteDialog
import ttldtor.ui.CjrMenuBar

class MainGui: Application() {
    override fun start(stage: Stage) {
        val pane = VBox()
        val menuBar = CjrMenuBar()
        val logSiteTable = LogSiteTable()
        val logSites = FXCollections.observableArrayList<LogSiteModel>()

        logSiteTable.visibleProperty().addListener({ observable, old, new ->
            menuBar.addLogSiteMenuItem.isDisable = !new
        })

        stage.title = "CJR";
        logSiteTable.isVisible = false
        logSiteTable.items = logSites
        pane.children.addAll(menuBar, logSiteTable)

        logSites.addAll(
                LogSiteModel(
                    "cpp",
                    "c_plus_plus@conference.jabber.ru",
                    "http://0xd34df00d.me/logs/chat/c_plus_plus@conference.jabber.ru"),
                LogSiteModel(
                    "codingteam",
                    "codingteam@conference.jabber.ru",
                    "http://0xd34df00d.me/logs/chat/codingteam@conference.jabber.ru")
        )

        menuBar.exitMenuItem.onAction = EventHandler {
            System.exit(0)
        }

        menuBar.viewLogSites.onAction = EventHandler {
            logSiteTable.isVisible = true
        }

        menuBar.addLogSiteMenuItem.onAction = EventHandler {
            val dialog = AddLogSiteDialog()

            val result =  dialog.showAndWait()

            result.ifPresent {
                val logSite = LogSiteDao.create(it)

                if (logSite != null) {
                    logSites.add(LogSiteModel(logSite))
                }
            }
        }

        menuBar.parseLogMenuItem.onAction = EventHandler {
            val selected = logSiteTable.selectionModel.selectedItems

            if (selected.isNotEmpty()) {
                runAsync {
                    ChatLogUrlsCollector().collect(selected[0].url)
                } ui {result ->
                    println(result.dateToUrl.size)


                }
            }
        }

        stage.scene = Scene(pane, 800.0, 600.0)
        stage.show()
    }
}

enum class BackupStrategy(val strategyName: String) {
    MIGRATION_BACKUP("migration_backup.db.zip"),
    SESSION_BACKUP("session_backup.db.zip")
}

fun backupDatabase(strategy: BackupStrategy):Boolean {
    val log = LoggerFactory.getLogger("Cjr")
    var backupIsCreated = true

    log.info("The backup creating is started. Strategy: ${strategy.name}")
    ConnectionPool.connection.use {conn ->
        try {
            Script.process(conn, "./db_backup/${strategy.strategyName}", "", "COMPRESSION ZIP");
        } catch (e: SQLException) {
            log.error("Could not backup the database. ", e)
            backupIsCreated = false
        }
    }
    log.info("The backup creating is finished: {}", if (backupIsCreated) "OK" else "FAIL")

    return backupIsCreated
}

fun restoreDatabase(strategy: BackupStrategy):Boolean {
    val log = LoggerFactory.getLogger("Cjr")
    var databaseIsRestored = true

    log.info("The database restoring is started. Strategy: ${strategy.name}")
    ConnectionPool.connection.use {conn ->
        try {
            conn.prepareStatement("RUNSCRIPT FROM './db_backup/${strategy.strategyName}' COMPRESSION ZIP").use {st ->
                st.execute()
            }
        } catch (e: SQLException) {
            log.error("Could not restore the database. ", e)
            databaseIsRestored = false
        }
    }
    log.info("The database restoring is finished: {}", if (databaseIsRestored) "OK" else "FAIL")

    return databaseIsRestored
}

fun main(args: Array<String>) {
    val log = LoggerFactory.getLogger("Cjr")

    Locale.setDefault(Config.locale)

    if (backupDatabase(BackupStrategy.MIGRATION_BACKUP)) {
        val flyway = Flyway()

        flyway.setDataSource(Config.databaseUrl, Config.user, Config.password)

        try {
            flyway.migrate()
            Application.launch(MainGui().javaClass, *args)
            backupDatabase(BackupStrategy.SESSION_BACKUP)
        } catch (e: FlywayException) {
            log.error("Could not migrate the database. ", e)

            flyway.clean()
            restoreDatabase(BackupStrategy.MIGRATION_BACKUP)
        }
    }

    log.warn("Exiting...")
}