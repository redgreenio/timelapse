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
  private const val MENU_FILE_MENU_ITEM_OPEN = "Open..."
  private const val MENU_FILE_MENU_ITEM_CLOSE_PROJECT = "Close Project"

  fun install(
    scene: Scene,
    currentRepositoryPath: String,
    refreshMenu: Boolean = false,
    listener: FileMenuItemsClickListener
  ) {
    val borderPane = scene.root as BorderPane
    borderPane.top = MenuBar().apply {
      useSystemMenuBarProperty().set(true)
      if (refreshMenu) {
        menus.clear()
      }
      menus.add(fileMenu(currentRepositoryPath, listener))
    }
  }

  private fun fileMenu(
    currentRepositoryPath: String,
    listener: FileMenuItemsClickListener
  ): Menu {
    return Menu(MENU_FILE).apply {
      mnemonicParsingProperty().set(true)

      val menuItems = mutableListOf(
        openMenuItem(listener),
        openRecentMenu(currentRepositoryPath, listener),
        projectCloseMenuItem(listener),
      )

      items.addAll(menuItems)
    }
  }

  private fun openMenuItem(listener: FileMenuItemsClickListener): MenuItem {
    return MenuItem(MENU_FILE_MENU_ITEM_OPEN).apply {
      setOnAction { listener.onOpenClicked() }
    }
  }

  private fun openRecentMenu(
    currentRepositoryPath: String,
    listener: FileMenuItemsClickListener
  ): Menu {
    return OpenRecentMenuViewModelUseCase(PreferencesRecentGitRepositoriesStorage())
      .invoke(currentRepositoryPath)
      .toJavaFxMenu(listener)
  }

  private fun projectCloseMenuItem(listener: FileMenuItemsClickListener): MenuItem {
    return MenuItem(MENU_FILE_MENU_ITEM_CLOSE_PROJECT).apply {
      setOnAction { listener.onCloseProjectClicked() }
    }
  }
}
