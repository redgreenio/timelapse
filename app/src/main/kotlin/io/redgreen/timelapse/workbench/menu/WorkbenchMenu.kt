package io.redgreen.timelapse.workbench.menu

import io.redgreen.timelapse.foo.closeWindow
import io.redgreen.timelapse.openrepo.storage.PreferencesRecentGitRepositoriesStorage
import io.redgreen.timelapse.openrepo.view.OpenRepoScene
import io.redgreen.timelapse.workbench.menu.javafx.toJavaFxMenu
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

object WorkbenchMenu {
  private const val MENU_FILE = "File"
  private const val MENU_FILE_MENU_ITEM_CLOSE_PROJECT = "Close Project"

  fun install(
    scene: Scene,
    currentRepositoryPath: String,
    refreshMenu: Boolean = false,
    listener: FileMenuItemsClickListener
  ) {
    val menuBar = MenuBar().apply {
      useSystemMenuBarProperty().set(true)
    }

    if (refreshMenu) {
      menuBar.menus.clear()
    }
    menuBar.menus.add(buildFileMenu(scene, currentRepositoryPath, listener))
    (scene.root as BorderPane).top = menuBar
  }

  private fun buildFileMenu(
    scene: Scene,
    currentRepositoryPath: String,
    listener: FileMenuItemsClickListener
  ): Menu {
    return Menu(MENU_FILE).apply {
      mnemonicParsingProperty().set(true)
      items.addAll(buildOpenRecentMenu(currentRepositoryPath, listener), buildProjectCloseMenuItem(scene, listener))
    }
  }

  private fun buildProjectCloseMenuItem(scene: Scene, listener: FileMenuItemsClickListener): MenuItem {
    return MenuItem(MENU_FILE_MENU_ITEM_CLOSE_PROJECT).apply {
      setOnAction {
        scene.closeWindow()
        OpenRepoScene.launch(Stage())
        println(listener)
      }
    }
  }

  private fun buildOpenRecentMenu(
    currentRepositoryPath: String,
    listener: FileMenuItemsClickListener
  ): Menu {
    return OpenRecentMenuViewModelUseCase(PreferencesRecentGitRepositoriesStorage())
      .invoke(currentRepositoryPath)
      .toJavaFxMenu(listener)
  }
}
