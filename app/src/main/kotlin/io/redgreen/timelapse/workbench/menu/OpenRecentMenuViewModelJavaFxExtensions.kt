package io.redgreen.timelapse.workbench.menu

import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel.Empty
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel.NonEmpty
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem

private const val MENU_FILE_MENU_OPEN_RECENT = "Open Recent"

@Suppress("unused") // Because, the type `Empty` itself provides us enough information.
fun Empty.toJavaFxMenu(): Menu {
  return Menu(MENU_FILE_MENU_OPEN_RECENT).apply {
    isDisable = true
  }
}

fun NonEmpty.toJavaFxMenu(): Menu {
  val menuItems = menuItemViewModels.map {
    when (it) {
      OpenRecentMenuItemViewModel.ClearRecent -> MenuItem("Clear Recent")
      is OpenRecentMenuItemViewModel.RecentRepository -> MenuItem(it.repositoryDirectory)
    }
  }
  return Menu(MENU_FILE_MENU_OPEN_RECENT).apply {
    this.items.addAll(menuItems)
  }
}

fun OpenRecentMenuViewModel.toJavaFxMenu(): Menu {
  return when (this) {
    Empty -> (this as Empty).toJavaFxMenu()
    is NonEmpty -> this.toJavaFxMenu()
  }
}
