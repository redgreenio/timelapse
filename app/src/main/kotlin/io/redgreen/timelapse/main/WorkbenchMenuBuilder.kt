package io.redgreen.timelapse.main

import io.redgreen.timelapse.foo.closeWindow
import io.redgreen.timelapse.openrepo.view.OpenRepoScene
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

class WorkbenchMenuBuilder {
  companion object {
    private const val MENU_FILE = "File"
    private const val MENU_ITEM_CLOSE_PROJECT = "Close Project"
  }

  fun installMenu(scene: Scene) {
    val menuBar = MenuBar().apply {
      useSystemMenuBarProperty().set(true)
    }

    menuBar.menus.add(buildFileMenu(scene))
    (scene.root as BorderPane).top = menuBar
  }

  private fun buildFileMenu(scene: Scene): Menu {
    val fileMenu = Menu(MENU_FILE)
    val closeProjectMenuItem = buildProjectCloseMenuItem(scene)
    fileMenu.items.add(closeProjectMenuItem)
    return fileMenu
  }

  private fun buildProjectCloseMenuItem(scene: Scene): MenuItem {
    return MenuItem(MENU_ITEM_CLOSE_PROJECT).apply {
      setOnAction {
        scene.closeWindow()
        OpenRepoScene.launch(Stage())
      }
    }
  }
}
