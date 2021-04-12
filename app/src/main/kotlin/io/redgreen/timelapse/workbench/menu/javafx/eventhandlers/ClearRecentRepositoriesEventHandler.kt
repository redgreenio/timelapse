package io.redgreen.timelapse.workbench.menu.javafx.eventhandlers

import io.redgreen.timelapse.foo.closeWindow
import io.redgreen.timelapse.main.TimelapseScene
import io.redgreen.timelapse.openrepo.storage.PreferencesRecentGitRepositoriesStorage
import io.redgreen.timelapse.workbench.menu.OpenRecentMenuItemsClickListener
import io.redgreen.timelapse.workbench.menu.WorkbenchMenu
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.stage.Stage

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
      object : OpenRecentMenuItemsClickListener {
        override fun onRecentRepositoryClicked(gitDirectoryPath: String) {
          scene.closeWindow()
          TimelapseScene.launch(Stage(), currentGitRepositoryPath)
        }
      }
    )
  }
}
