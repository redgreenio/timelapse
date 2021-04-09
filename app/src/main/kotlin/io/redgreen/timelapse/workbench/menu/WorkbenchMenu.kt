package io.redgreen.timelapse.workbench.menu

import io.redgreen.timelapse.foo.closeWindow
import io.redgreen.timelapse.openrepo.view.OpenRepoScene
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel.NonEmpty
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

object WorkbenchMenu {
  private const val MENU_FILE = "File"
  private const val MENU_ITEM_CLOSE_PROJECT = "Close Project"

  private const val FEATURE_FLAG_SHOW_OPEN_RECENT_TO_USER = false

  fun install(scene: Scene) {
    val menuBar = MenuBar().apply {
      useSystemMenuBarProperty().set(true)
    }

    menuBar.menus.add(buildFileMenu(scene))
    (scene.root as BorderPane).top = menuBar
  }

  private fun buildFileMenu(scene: Scene): Menu {
    return Menu(MENU_FILE).apply {
      mnemonicParsingProperty().set(true)

      if (FEATURE_FLAG_SHOW_OPEN_RECENT_TO_USER) {
        items.addAll(buildOpenRecentMenu(), buildProjectCloseMenuItem(scene))
      } else {
        items.add(buildProjectCloseMenuItem(scene))
      }
    }
  }

  private fun buildProjectCloseMenuItem(scene: Scene): MenuItem {
    return MenuItem(MENU_ITEM_CLOSE_PROJECT).apply {
      setOnAction {
        scene.closeWindow()
        OpenRepoScene.launch(Stage())
      }
    }
  }

  private fun buildOpenRecentMenu(): Menu {
    val menuItemViewModels = listOf("Shopping", "Coffee")
      .map(OpenRecentMenuItemViewModel::RecentRepository) + OpenRecentMenuItemViewModel.ClearRecent
    return (NonEmpty(menuItemViewModels) as OpenRecentMenuViewModel).toJavaFxMenu()
  }
}
