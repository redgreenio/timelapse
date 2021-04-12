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
    currentGitRepositoryPath: String,
    refreshMenu: Boolean = false
  ) {
    val menuBar = MenuBar().apply {
      useSystemMenuBarProperty().set(true)
    }

    if (refreshMenu) {
      menuBar.menus.clear()
    }
    menuBar.menus.add(buildFileMenu(scene, currentGitRepositoryPath, object : OpenRecentMenuItemsClickListener {}))
    (scene.root as BorderPane).top = menuBar
  }

  private fun buildFileMenu(
    scene: Scene,
    currentGitRepositoryPath: String,
    listener: OpenRecentMenuItemsClickListener
  ): Menu {
    return Menu(MENU_FILE).apply {
      mnemonicParsingProperty().set(true)
      items.addAll(buildOpenRecentMenu(scene, currentGitRepositoryPath, listener), buildProjectCloseMenuItem(scene))
    }
  }

  private fun buildProjectCloseMenuItem(scene: Scene): MenuItem {
    return MenuItem(MENU_FILE_MENU_ITEM_CLOSE_PROJECT).apply {
      setOnAction {
        scene.closeWindow()
        OpenRepoScene.launch(Stage())
      }
    }
  }

  private fun buildOpenRecentMenu(
    scene: Scene,
    currentGitRepositoryPath: String,
    listener: OpenRecentMenuItemsClickListener
  ): Menu {
    return OpenRecentMenuViewModelUseCase(PreferencesRecentGitRepositoriesStorage())
      .invoke(currentGitRepositoryPath)
      .toJavaFxMenu(scene, currentGitRepositoryPath, listener)
  }
}
