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

enum class EventType {Message, ThirdPersonMessage, Enter, Exit}

interface IEvent {
    val type: EventType
    val timestamp: Long
    val who: String
}

interface IMessageEvent: IEvent {
    val message: String
}

class EnterEvent(override val type: EventType = EventType.Enter, override val timestamp: Long, override val who: String) : IEvent
class ExitEvent(override val type: EventType = EventType.Exit, override val timestamp: Long, override val who: String) : IEvent
class MessageEvent(override val message: String, override val type: EventType = EventType.Message, override val timestamp: Long, override val who: String) : IMessageEvent
class ThirdPersonMessageEvent(override val message: String, override val type: EventType = EventType.ThirdPersonMessage, override val timestamp: Long, override val who: String) : IMessageEvent

fun parseEvent(): IEvent? {
    return null;
}

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