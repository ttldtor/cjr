package ttldtor

import javafx.application.Application;
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.MenuBar
import javafx.scene.layout.VBox
import javafx.stage.Stage
import ttldtor.javafx.models.LogSiteModel
import ttldtor.javafx.tables.LogSiteTable

fun createMenuBar(): MenuBar {
    val menuBar = CjrMenuBar()

    menuBar.exitMenuItem.onAction = EventHandler<ActionEvent> {
        a -> System.exit(0)
    }

    return menuBar
}

class MainGui: Application() {
    override fun start(stage: Stage) {
        val data = FXCollections.observableArrayList<LogSiteModel>(
                LogSiteModel("cpp",
                        "c_plus_plus@conference.jabber.ru",
                        "http://0xd34df00d.me/logs/chat/c_plus_plus@conference.jabber.ru"),
                LogSiteModel("codingteam",
                        "codingteam@conference.jabber.ru",
                        "http://0xd34df00d.me/logs/chat/codingteam@conference.jabber.ru")
                )

        stage.title = "CJR";

        val pane = VBox()

        pane.children.addAll(createMenuBar())

        val logSiteTable = LogSiteTable()

        logSiteTable.items = data

        pane.children.add(logSiteTable)

        stage.scene = Scene(pane, 800.0, 600.0)
        stage.show()
    }
}

fun main(args: Array<String>) {
    print("TEST")
    Application.launch(MainGui().javaClass, *args)
}