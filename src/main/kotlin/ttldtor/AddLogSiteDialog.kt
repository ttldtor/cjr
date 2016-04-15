package ttldtor

import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.util.Callback
import ttldtor.poko.LogSite

class AddLogSiteDialog(): Dialog<LogSite>() {
    val grid = GridPane()
    val nameLabel = Label("Name:")
    val nameText = TextField()
    val conferenceLabel = Label("Conference:")
    val conferenceText = TextField()
    val urlLabel = Label("Url:")
    val urlText = TextField()

    init {
        title = "Add log site"
        headerText = "Enter log site parameters"
        grid.addRow(0, nameLabel, nameText)
        grid.addRow(1, conferenceLabel, conferenceText)
        grid.addRow(2, urlLabel, urlText)

        resultConverter = Callback {
            if (it == ButtonType.OK) {
                return@Callback LogSite(name = nameText.text, conference = conferenceText.text, url = urlText.text);
            }

            return@Callback null;
        };

        dialogPane.buttonTypes.addAll(ButtonType.OK);
        dialogPane.content = grid;
    }
}