package ttldtor.ui.javafx.models

import javafx.beans.property.*
import ttldtor.poko.LogSite

class LogSiteModel {
    private val _name: SimpleStringProperty = SimpleStringProperty()
    var name: String
        get() = _name.value
        set(value: String) {
            _name.value = value
        }

    private val _conference: SimpleStringProperty = SimpleStringProperty()
    var conference: String
        get() = _conference.value
        set(value: String) {
            _conference.value = value
        }

    private val _url: SimpleStringProperty = SimpleStringProperty()
    var url: String
        get() = _url.value
        set(value: String) {
            _url.value = value
        }

    constructor(name: String, conference: String, url: String) {
        this.name = name
        this.conference = conference
        this.url = url
    }

    constructor(poko: LogSite) {
        this.name = poko.name
        this.conference = poko.conference
        this.url = poko.url
    }
}