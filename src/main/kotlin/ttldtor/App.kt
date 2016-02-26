package ttldtor

import javafx.application.Application;
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.layout.VBox
import javafx.stage.Stage

fun createMenuBar(): MenuBar {
    val menuBar = MenuBar()
    val menuFile = Menu("File")

    val exitItem = MenuItem("Exit")

    exitItem.onAction = EventHandler<ActionEvent> {
        a -> System.exit(0)
    }

    menuFile.items.addAll(exitItem)

    menuBar.menus.addAll(menuFile, Menu("Edit"))

    return menuBar
}

class MainGui: Application() {
    override fun start(stage: Stage) {
        stage.title = "Test";

        val pane = VBox()

        pane.children.addAll(createMenuBar())

        stage.scene = Scene(pane, 800.0, 600.0)
        stage.show()
    }
}

fun main(args: Array<String>) {
    print("TEST")
    Application.launch(MainGui().javaClass, *args)
}