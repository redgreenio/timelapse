package io.redgreen.timelapse.workbench.menu

import io.redgreen.timelapse.openrepo.storage.PreferencesRecentGitRepositoriesStorage
import io.redgreen.timelapse.workbench.menu.javafx.toJavaFxMenu
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.layout.BorderPane

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
    menuBar.menus.add(buildFileMenu(currentRepositoryPath, listener))
    (scene.root as BorderPane).top = menuBar
  }

  private fun buildFileMenu(
    currentRepositoryPath: String,
    listener: FileMenuItemsClickListener
  ): Menu {
    return Menu(MENU_FILE).apply {
      mnemonicParsingProperty().set(true)
      items.addAll(buildOpenRecentMenu(currentRepositoryPath, listener), buildProjectCloseMenuItem(listener))
    }
  }

  private fun buildProjectCloseMenuItem(listener: FileMenuItemsClickListener): MenuItem {
    return MenuItem(MENU_FILE_MENU_ITEM_CLOSE_PROJECT).apply {
      setOnAction { listener.onCloseProjectClicked() }
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
