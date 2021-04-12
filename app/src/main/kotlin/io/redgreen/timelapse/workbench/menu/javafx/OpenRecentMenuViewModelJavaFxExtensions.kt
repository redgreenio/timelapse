package io.redgreen.timelapse.workbench.menu.javafx

import io.redgreen.timelapse.workbench.menu.ClearRecentMenuItemViewModel
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuItemViewModel
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel.Empty
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel.NonEmpty
import io.redgreen.timelapse.workbench.menu.RecentRepository
import io.redgreen.timelapse.workbench.menu.SeparatorMenuItemViewModel
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem

private const val MENU_FILE_MENU_OPEN_RECENT = "Open Recent"
private const val MENU_OPEN_MENU_ITEM_CLEAR_RECENT = "Clear Recent"

fun OpenRecentMenuViewModel.toJavaFxMenu(scene: Scene): Menu = when (this) {
  Empty -> (this as Empty).toJavaFxMenu()
  is NonEmpty -> this.toJavaFxMenu(scene)
}

@Suppress("unused") // Because, the type `Empty` itself provides us enough information.
private fun Empty.toJavaFxMenu(): Menu {
  return Menu(MENU_FILE_MENU_OPEN_RECENT).apply {
    isDisable = true
  }
}

private fun NonEmpty.toJavaFxMenu(scene: Scene): Menu {
  val menuItems = menuItemViewModels.map { toMenuItem(it, scene) }
  return Menu(MENU_FILE_MENU_OPEN_RECENT).apply {
    this.items.addAll(menuItems)
  }
}

private fun toMenuItem(
  menuItemViewModel: OpenRecentMenuItemViewModel,
  scene: Scene
): MenuItem = when (menuItemViewModel) {
  ClearRecentMenuItemViewModel -> MenuItem(MENU_OPEN_MENU_ITEM_CLEAR_RECENT).apply {
    onAction = ClearRecentRepositoriesEventHandler(scene)
  }
  is RecentRepository -> MenuItem(menuItemViewModel.repositoryDirectory).apply {
    isDisable = !menuItemViewModel.isPresent
    onAction = OpenRecentRepositoryEventHandler(scene, menuItemViewModel.repositoryDirectory)
  }
  SeparatorMenuItemViewModel -> SeparatorMenuItem()
}
