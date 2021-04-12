package io.redgreen.timelapse.workbench.menu.javafx.eventhandlers

import io.redgreen.timelapse.openrepo.storage.PreferencesRecentGitRepositoriesStorage
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuItemsClickListener
import io.redgreen.timelapse.workbench.menu.WorkbenchMenu
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Scene

class ClearRecentRepositoriesEventHandler(
  private val scene: Scene,
  private val currentGitRepositoryPath: String
) : EventHandler<ActionEvent> {
  override fun handle(event: ActionEvent) {
    PreferencesRecentGitRepositoriesStorage().clearRecentRepositories(currentGitRepositoryPath)
    WorkbenchMenu.install(
      scene,
      currentGitRepositoryPath,
      true,
      object : OpenRecentMenuItemsClickListener {}
    )
  }
}
