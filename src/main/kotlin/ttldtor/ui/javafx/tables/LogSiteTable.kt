package ttldtor.ui.javafx.tables
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.util.Callback
import ttldtor.ui.javafx.models.LogSiteModel
import java.text.SimpleDateFormat

class LogSiteTable(): TableView<LogSiteModel>() {
    private val idColumn = TableColumn<LogSiteModel, Long>("Id").apply {
        isVisible = false
    }

    private val nameColumn = TableColumn<LogSiteModel, String>("Name")
    private val conferenceColumn = TableColumn<LogSiteModel, String>("Conference")
    private val urlColumn = TableColumn<LogSiteModel, String>("URL")
    private val lastUpdateColumn = TableColumn<LogSiteModel, String>("Last update")

    init {
        idColumn.cellValueFactory = PropertyValueFactory<LogSiteModel, Long>("id")
        nameColumn.cellValueFactory = PropertyValueFactory<LogSiteModel, String>("name")
        conferenceColumn.cellValueFactory = PropertyValueFactory<LogSiteModel, String>("conference")
        urlColumn.cellValueFactory = PropertyValueFactory<LogSiteModel, String>("url")
        lastUpdateColumn.cellValueFactory = Callback {v ->
            return@Callback SimpleStringProperty().apply {
                value = SimpleDateFormat("dd.MM.yyyy").format(v.value.lastParsedDate)
            }
        }

        columns.setAll(idColumn, nameColumn, conferenceColumn, urlColumn, lastUpdateColumn)
    }
}