package ttldtor.ui.javafx.models

import javafx.beans.property.*
import ttldtor.poko.LogSite

class LogSiteModel {
    private val _id = SimpleLongProperty()
    var id: Long
        get() = _id.value
        set(value: Long) {
            _id.value = value
        }

    private val _name = SimpleStringProperty()
    var name: String
        get() = _name.value
        set(value: String) {
            _name.value = value
        }

    private val _conference = SimpleStringProperty()
    var conference: String
        get() = _conference.value
        set(value: String) {
            _conference.value = value
        }

    private val _url = SimpleStringProperty()
    var url: String
        get() = _url.value
        set(value: String) {
            _url.value = value
        }

    constructor(id: Long, name: String, conference: String, url: String) {
        this.id = id
        this.name = name
        this.conference = conference
        this.url = url
    }

    constructor(poko: LogSite) {
        this.id = poko.id
        this.name = poko.name
        this.conference = poko.conference
        this.url = poko.url
    }

    fun set(poko: LogSite) {
        this.id = poko.id
        this.name = poko.name
        this.conference = poko.conference
        this.url = poko.url
    }
}