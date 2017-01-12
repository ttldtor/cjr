package ttldtor

import javafx.application.Application
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.layout.VBox
import javafx.stage.Stage
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.FlywayException
import org.h2.tools.Script
import org.slf4j.LoggerFactory
import ttldtor.collectors.ChatLogUrlsCollector
import ttldtor.dao.LogSiteDao
import ttldtor.ui.CjrMenuBar
import ttldtor.ui.DialogType
import ttldtor.ui.LogSiteDialog
import ttldtor.ui.javafx.models.LogSiteModel
import ttldtor.ui.javafx.runAsync
import ttldtor.ui.javafx.tables.LogSiteTable
import ttldtor.ui.javafx.ui
import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.*

class MainGui: Application() {
    override fun start(stage: Stage) {
        val pane = VBox()
        val menuBar = CjrMenuBar()
        val logSiteTable = LogSiteTable()
        val logSites = FXCollections.observableArrayList<LogSiteModel>()

        logSiteTable.visibleProperty().addListener({ observable, old, new ->
            menuBar.addLogSiteMenuItem.isDisable = !new
            menuBar.editLogSiteMenuItem.isDisable = !new
            menuBar.deleteLogSiteMenuItem.isDisable = !new
        })

        stage.title = "CJR"
        logSiteTable.isVisible = false
        logSiteTable.items = logSites
        pane.children.addAll(menuBar, logSiteTable)

        logSites.addAll(LogSiteDao.get().map(::LogSiteModel))

        menuBar.exitMenuItem.onAction = EventHandler {
            Platform.exit()
        }

        menuBar.viewLogSites.onAction = EventHandler {
            logSiteTable.isVisible = true
        }

        menuBar.addLogSiteMenuItem.onAction = EventHandler {
            val dialog = LogSiteDialog(DialogType.NEW, null)
            val result =  dialog.showAndWait()

            result.ifPresent {
                val logSite = LogSiteDao.create(it)

                if (logSite != null) {
                    logSites.add(LogSiteModel(logSite))
                }
            }
        }

        val editSelected = fun ():Unit {
            val selected = logSiteTable.selectionModel.selectedItem

            if (selected != null) {
                val logSite = LogSiteDao.getById(selected.id)

                if (logSite == null) {
                    logSites.remove(selected)

                    return
                }

                val dialog = LogSiteDialog(DialogType.EDIT, logSite)
                val result = dialog.showAndWait()

                result.ifPresent {
                    if (LogSiteDao.save(it)) {
                        selected.set(it)
                    }
                }
            }
        }

        logSiteTable.onMouseClicked = EventHandler {e ->
            if (e.clickCount > 1) {
                editSelected()
            }
        }

        menuBar.editLogSiteMenuItem.onAction = EventHandler { editSelected() }

        menuBar.deleteLogSiteMenuItem.onAction = EventHandler {
            val selected = logSiteTable.selectionModel.selectedItem
            val logSite = LogSiteDao.getById(selected.id)

            if (logSite == null) {
                logSites.remove(selected)
            } else {
                val confirm = Alert(Alert.AlertType.CONFIRMATION)

                confirm.title = "Delete entity"
                confirm.headerText = "Do you want to delete this log site record?"
                confirm.showAndWait().ifPresent {
                    if (it == ButtonType.OK && LogSiteDao.delete(logSite)) {
                        logSites.remove(selected)
                    }
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
            Script.process(conn, "./db_backup/${strategy.strategyName}", "", "COMPRESSION ZIP")
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
            conn.prepareStatement("RUNSCRIPT FROM './db_backup/${strategy.strategyName}' COMPRESSION ZIP")
                    .use(PreparedStatement::execute)
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