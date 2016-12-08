package ttldtor.ui.javafx.models

import javafx.beans.property.*
import ttldtor.poko.LogSite
import java.util.*

class LogSiteModel(logSite: LogSite) {
    private val _id = SimpleLongProperty()
    var id: Long
        get() = _id.value
        set(value) {
            _id.value = value
        }

    private val _name = SimpleStringProperty()
    var name: String
        get() = _name.value
        set(value) {
            _name.value = value
        }

    private val _conference = SimpleStringProperty()
    var conference: String
        get() = _conference.value
        set(value) {
            _conference.value = value
        }

    private val _url = SimpleStringProperty()
    var url: String
        get() = _url.value
        set(value) {
            _url.value = value
        }

    private val _lastParsedDate = SimpleObjectProperty<Date>()
    var lastParsedDate: Date
        get() = _lastParsedDate.value
        set(value) {
            _lastParsedDate.value = value
        }

    fun set(poko: LogSite) {
        this.id = poko.id
        this.name = poko.name
        this.conference = poko.conference
        this.url = poko.url
        this.lastParsedDate = poko.lastParsedDate
    }

    init {
        this.id = logSite.id
        this.name = logSite.name
        this.conference = logSite.conference
        this.url = logSite.url
        this.lastParsedDate = logSite.lastParsedDate
    }
}