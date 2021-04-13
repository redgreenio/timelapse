package io.redgreen.timelapse.workbench.menu.javafx

import io.redgreen.timelapse.workbench.menu.ClearRecentMenuItemViewModel
import io.redgreen.timelapse.workbench.menu.EmptyMenuViewModel
import io.redgreen.timelapse.workbench.menu.FileMenuItemsClickListener
import io.redgreen.timelapse.workbench.menu.NonEmptyMenuViewModel
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuItemViewModel
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel
import io.redgreen.timelapse.workbench.menu.RecentRepositoryMenuItemViewModel
import io.redgreen.timelapse.workbench.menu.SeparatorMenuItemViewModel
import javafx.event.EventHandler
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem

private const val MENU_FILE_MENU_OPEN_RECENT = "Open Recent"
private const val MENU_OPEN_MENU_ITEM_CLEAR_RECENT = "Clear Recent"

fun OpenRecentMenuViewModel.toJavaFxMenu(listener: FileMenuItemsClickListener): Menu = when (this) {
  EmptyMenuViewModel -> (this as EmptyMenuViewModel).toJavaFxMenu()
  is NonEmptyMenuViewModel -> this.toJavaFxMenu(listener)
}

@Suppress("unused") // Because, the type `Empty` itself provides us enough information.
private fun EmptyMenuViewModel.toJavaFxMenu(): Menu {
  return Menu(MENU_FILE_MENU_OPEN_RECENT).apply {
    isDisable = true
  }
}

private fun NonEmptyMenuViewModel.toJavaFxMenu(listener: FileMenuItemsClickListener): Menu {
  val menuItems = menuItemViewModels
    .map { toMenuItem(it, listener) }
  return Menu(MENU_FILE_MENU_OPEN_RECENT).apply {
    this.items.addAll(menuItems)
  }
}

private fun toMenuItem(
  menuItemViewModel: OpenRecentMenuItemViewModel,
  listener: FileMenuItemsClickListener
): MenuItem = when (menuItemViewModel) {
  ClearRecentMenuItemViewModel -> MenuItem(MENU_OPEN_MENU_ITEM_CLEAR_RECENT).apply {
    onAction = EventHandler { listener.onClearRecentClicked() }
  }
  is RecentRepositoryMenuItemViewModel -> MenuItem(menuItemViewModel.recentRepository.title()).apply {
    isDisable = !menuItemViewModel.isPresent
    onAction = EventHandler { listener.onRecentClicked(menuItemViewModel.recentRepository.path) }
  }
  SeparatorMenuItemViewModel -> SeparatorMenuItem()
}
