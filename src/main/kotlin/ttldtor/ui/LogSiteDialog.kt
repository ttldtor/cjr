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

class LogSiteDialog(dialogType: DialogType, val initValue: LogSite?): Dialog<LogSite>() {
    val grid = GridPane()
    val nameLabel = Label("Name:")
    val nameText = TextField()
    val conferenceLabel = Label("Conference:")
    val conferenceText = TextField()
    val urlLabel = Label("Url:")
    val urlText = TextField()

    init {
        var readOnly = false

        when (dialogType) {
            DialogType.NEW -> {
                title = "Add log site"
                headerText = "Enter log site parameters"
            }

            DialogType.EDIT -> {
                title = "Edit log site"
                headerText = "Enter log site parameters"
            }

            else -> {
                title = "Log site"
                headerText = "Log site parameters"
                readOnly = true
            }
        }

        nameText.text = initValue?.name ?: ""
        conferenceText.text = initValue?.conference ?: ""
        urlText.text = initValue?.url ?: ""

        nameText.isEditable = !readOnly
        conferenceText.isEditable = !readOnly
        urlText.isEditable = !readOnly

        grid.addRow(0, nameLabel, nameText)
        grid.addRow(1, conferenceLabel, conferenceText)
        grid.addRow(2, urlLabel, urlText)

        resultConverter = Callback {
            if (readOnly) {
                return@Callback initValue
            }

            if (it == ButtonType.OK) {
                return@Callback LogSite(
                        id = initValue?.id ?: -1,
                        name = nameText.text,
                        conference = conferenceText.text,
                        url = urlText.text,
                        lastParsedDate = initValue?.lastParsedDate ?: Date(0),
                        deleted = false
                )
            }

            return@Callback initValue
        }

        if (!readOnly) {
            val onTextChange = ChangeListener<String> { value, old, new ->
                //TODO: add validation

                dialogPane.lookupButton(ButtonType.OK).isDisable = nameText.text.isEmpty()
                        && conferenceText.text.isEmpty()
                        && urlText.text.isEmpty()
            }

            nameText.textProperty().addListener(onTextChange)
            conferenceText.textProperty().addListener(onTextChange)
            urlText.textProperty().addListener(onTextChange)
        }

        dialogPane.buttonTypes.addAll(ButtonType.OK)
        dialogPane.lookupButton(ButtonType.OK).isDisable = !readOnly
        dialogPane.content = grid
    }
}