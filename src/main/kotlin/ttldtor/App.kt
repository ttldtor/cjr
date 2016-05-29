package ttldtor

import javafx.application.Application;
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.stage.Stage
import ttldtor.javafx.models.LogSiteModel
import ttldtor.javafx.tables.LogSiteTable
import org.flywaydb.core.Flyway
import ttldtor.dao.LogSiteDao
import ttldtor.javafx.runAsync
import ttldtor.javafx.ui
import java.util.*

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

fun main(args: Array<String>) {
    Locale.setDefault(Config.locale)

    val flyway = Flyway()

    flyway.setDataSource(Config.databaseUrl, Config.user, Config.password)
    flyway.migrate()

    Application.launch(MainGui().javaClass, *args)
}