package io.redgreen.timelapse.workbench.menu

import io.redgreen.timelapse.foo.closeWindow
import io.redgreen.timelapse.main.TimelapseScene
import io.redgreen.timelapse.openrepo.storage.PreferencesRecentGitRepositoriesStorage
import javafx.scene.Scene
import javafx.stage.Stage

interface OpenRecentMenuItemsClickListener {
  fun onClearRecentClicked()
  fun onRecentRepositoryClicked(directoryPath: String)
}

class DefaultOpenRecentMenuItemsClickListener(
  private val scene: Scene,
  private val openRepositoryPath: String
) : OpenRecentMenuItemsClickListener {
  override fun onRecentRepositoryClicked(directoryPath: String) {
    scene.closeWindow()
    TimelapseScene.launch(Stage(), directoryPath)
  }

  override fun onClearRecentClicked() {
    PreferencesRecentGitRepositoriesStorage().clearRecentRepositories(openRepositoryPath)
    WorkbenchMenu.install(scene, openRepositoryPath, true, this)
  }
}
