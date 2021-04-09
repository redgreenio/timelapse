package io.redgreen.timelapse.workbench.menu

import io.redgreen.timelapse.workbench.menu.OpenRecentMenuViewModel.Empty
import javafx.scene.control.Menu

private const val MENU_FILE_MENU_OPEN_RECENT = "Open Recent"

@Suppress("unused") // Because, the type `Empty` itself provides us enough information.
fun Empty.toJavaFxMenu() : Menu {
  return Menu(MENU_FILE_MENU_OPEN_RECENT).apply {
    isDisable = true
  }
}
