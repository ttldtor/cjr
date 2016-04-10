package ttldtor.javafx.tables
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import ttldtor.javafx.models.LogSiteModel

class LogSiteTable(): TableView<LogSiteModel>() {
    private val nameColumn: TableColumn<LogSiteModel, String> = TableColumn("Name")
    private val conferenceColumn: TableColumn<LogSiteModel, String> = TableColumn("Conference")
    private val urlColumn: TableColumn<LogSiteModel, String> = TableColumn("URL")

    init {
        nameColumn.cellValueFactory = PropertyValueFactory<LogSiteModel, String>("name")
        conferenceColumn.cellValueFactory = PropertyValueFactory<LogSiteModel, String>("conference")
        urlColumn.cellValueFactory = PropertyValueFactory<LogSiteModel, String>("url")

        columns.setAll(nameColumn, conferenceColumn, urlColumn)
    }
}