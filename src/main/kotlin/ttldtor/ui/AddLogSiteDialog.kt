package ttldtor.ui

import javafx.beans.value.ChangeListener
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.util.Callback
import ttldtor.poko.LogSite
import java.util.*

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
                return@Callback LogSite(
                        id = -1,
                        name = nameText.text,
                        conference = conferenceText.text,
                        url = urlText.text, lastParsedDate = Date(),
                        deleted = false
                );
            }

            return@Callback null;
        };

        val onTextChange = ChangeListener<String> { value, old, new ->
            dialogPane.lookupButton(ButtonType.OK).isDisable = nameText.text.isEmpty()
                    && conferenceText.text.isEmpty()
                    && urlText.text.isEmpty()
        }

        nameText.textProperty().addListener(onTextChange)
        conferenceText.textProperty().addListener(onTextChange)
        urlText.textProperty().addListener(onTextChange)

        dialogPane.buttonTypes.addAll(ButtonType.OK);
        dialogPane.lookupButton(ButtonType.OK).isDisable = true
        dialogPane.content = grid;
    }
}