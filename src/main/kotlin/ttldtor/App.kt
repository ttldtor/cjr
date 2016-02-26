package ttldtor

import javafx.application.Application;
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.stage.Stage

class MainGui: Application() {
    override fun start(stage: Stage) {
        stage.title = "Test";

        val pane = StackPane()

        stage.scene = Scene(pane, 800.0, 600.0)
        stage.show()
    }
}

fun main(args: Array<String>) {
    print("TEST")
    Application.launch(MainGui().javaClass, *args)
}