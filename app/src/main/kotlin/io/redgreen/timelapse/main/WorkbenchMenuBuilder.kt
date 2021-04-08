package io.redgreen.timelapse.main

import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.layout.BorderPane

class WorkbenchMenuBuilder {
  companion object {
    private const val MENU_FILE = "File"
    private const val MENU_ITEM_CLOSE_PROJECT = "Close Project"
  }

  fun installMenu(scene: Scene) {
    val menuBar = MenuBar().apply {
      useSystemMenuBarProperty().set(true)
    }

    menuBar.menus.add(buildFileMenu())
    (scene.root as BorderPane).top = menuBar
  }

  private fun buildFileMenu(): Menu {
    val fileMenu = Menu(MENU_FILE)
    val closeProjectMenuItem = MenuItem(MENU_ITEM_CLOSE_PROJECT)
    fileMenu.items.add(closeProjectMenuItem)
    return fileMenu
  }
}
