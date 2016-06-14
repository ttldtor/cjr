package ttldtor.ui

import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem

class CjrMenuBar(): MenuBar() {
    val fileMenu = Menu("File")
    val exitMenuItem = MenuItem("Exit")

    val editMenu = Menu("Edit")
    val addLogSiteMenuItem = MenuItem("Add log site")
    val editLogSiteMenuItem = MenuItem("Edit log site")
    val deleteLogSiteMenuItem = MenuItem("Delete log site")
    val addNotifier = MenuItem("Add notifier")
    val parseLogMenuItem = MenuItem("Parse log")

    val viewMenu = Menu("View")
    val viewLogSites = MenuItem("Log sites")
    val viewNotifiers = MenuItem("Notifiers")

    init {
        fileMenu.items.addAll(exitMenuItem)
        editMenu.items.addAll(
                addLogSiteMenuItem,
                editLogSiteMenuItem,
                deleteLogSiteMenuItem,
                SeparatorMenuItem(),
                addNotifier,
                parseLogMenuItem)
        viewMenu.items.addAll(viewLogSites, viewNotifiers)
        this.menus.addAll(fileMenu, editMenu, viewMenu)
    }
}