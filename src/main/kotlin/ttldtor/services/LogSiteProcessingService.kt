package ttldtor.services

import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import ttldtor.ui.javafx.models.LogSiteModel

class LogSiteProcessingService(logSiteModels: ObservableList<LogSiteModel>) {
    val mainTask: Runnable = Runnable {
        for (model in logSiteModels) {
            //model.
        }

        Thread.`yield`()
    }

    init {
        logSiteModels.addListener(ListChangeListener { change ->
            while (change.next()) {
                if (change.wasAdded()) {

                }
            }
        })
    }

    fun process() {

    }
}