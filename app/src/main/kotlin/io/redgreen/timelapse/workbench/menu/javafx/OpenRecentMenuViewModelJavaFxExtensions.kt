package io.redgreen.timelapse.workbench.menu.javafx

import io.redgreen.timelapse.workbench.menu.ClearRecentMenuItemViewModel
import io.redgreen.timelapse.workbench.menu.EmptyMenuViewModel
import io.redgreen.timelapse.workbench.menu.NonEmptyMenuViewModel
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuItemViewModel
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuItemsClickListener
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel
import io.redgreen.timelapse.workbench.menu.RecentRepositoryMenuItemViewModel
import io.redgreen.timelapse.workbench.menu.SeparatorMenuItemViewModel
import io.redgreen.timelapse.workbench.menu.javafx.eventhandlers.ClearRecentRepositoriesEventHandler
import io.redgreen.timelapse.workbench.menu.javafx.eventhandlers.OpenRecentRepositoryEventHandler
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem

private const val MENU_FILE_MENU_OPEN_RECENT = "Open Recent"
private const val MENU_OPEN_MENU_ITEM_CLEAR_RECENT = "Clear Recent"

fun OpenRecentMenuViewModel.toJavaFxMenu(
  scene: Scene,
  currentGitRepositoryPath: String,
  listener: OpenRecentMenuItemsClickListener
): Menu = when (this) {
  EmptyMenuViewModel -> (this as EmptyMenuViewModel).toJavaFxMenu()
  is NonEmptyMenuViewModel -> this.toJavaFxMenu(scene, currentGitRepositoryPath, listener)
}

@Suppress("unused") // Because, the type `Empty` itself provides us enough information.
private fun EmptyMenuViewModel.toJavaFxMenu(): Menu {
  return Menu(MENU_FILE_MENU_OPEN_RECENT).apply {
    isDisable = true
  }
}

private fun NonEmptyMenuViewModel.toJavaFxMenu(
  scene: Scene,
  currentGitRepositoryPath: String,
  listener: OpenRecentMenuItemsClickListener
): Menu {
  val menuItems = menuItemViewModels
    .map { toMenuItem(it, scene, currentGitRepositoryPath, listener) }
  return Menu(MENU_FILE_MENU_OPEN_RECENT).apply {
    this.items.addAll(menuItems)
  }
}

private fun toMenuItem(
  menuItemViewModel: OpenRecentMenuItemViewModel,
  scene: Scene,
  currentGitRepositoryPath: String,
  listener: OpenRecentMenuItemsClickListener
): MenuItem = when (menuItemViewModel) {
  ClearRecentMenuItemViewModel -> MenuItem(MENU_OPEN_MENU_ITEM_CLEAR_RECENT).apply {
    onAction = ClearRecentRepositoriesEventHandler(scene, currentGitRepositoryPath)
    println(listener)
  }
  is RecentRepositoryMenuItemViewModel -> MenuItem(menuItemViewModel.recentRepository.title()).apply {
    isDisable = !menuItemViewModel.isPresent
    onAction = OpenRecentRepositoryEventHandler(scene, menuItemViewModel.recentRepository.path)
  }
  SeparatorMenuItemViewModel -> SeparatorMenuItem()
}
