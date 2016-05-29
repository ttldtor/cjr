package ttldtor

import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import ttldtor.javafx.models.LogSiteModel

class LogSiteProcessingService(logSiteModels: ObservableList<LogSiteModel>) {
    init {
        logSiteModels.addListener(ListChangeListener { change->
            while(change.next()) {
                if (change.wasAdded()) {

                }
            }
        })
    }
}