package io.redgreen.timelapse.workbench.menu.javafx

import io.redgreen.timelapse.openrepo.storage.PreferencesRecentGitRepositoriesStorage
import io.redgreen.timelapse.workbench.menu.WorkbenchMenu
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Scene

class ClearRecentRepositoriesEventHandler(private val scene: Scene) : EventHandler<ActionEvent> {
  override fun handle(event: ActionEvent) {
    PreferencesRecentGitRepositoriesStorage().clearRecentRepositories()
    WorkbenchMenu.install(scene, "", true)
  }
}
