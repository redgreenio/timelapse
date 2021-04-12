package io.redgreen.timelapse.workbench.menu

import io.redgreen.timelapse.foo.closeWindow
import io.redgreen.timelapse.main.TimelapseScene
import io.redgreen.timelapse.openrepo.storage.PreferencesRecentGitRepositoriesStorage
import io.redgreen.timelapse.openrepo.view.OpenRepoScene
import javafx.scene.Scene
import javafx.stage.Stage

class DefaultFileMenuItemsClickListener(
  private val scene: Scene,
  private val openRepositoryPath: String
) : FileMenuItemsClickListener {
  override fun onRecentRepositoryClicked(directoryPath: String) {
    scene.closeWindow()
    TimelapseScene.launch(Stage(), directoryPath)
  }

  override fun onClearRecentClicked() {
    PreferencesRecentGitRepositoriesStorage().clearRecentRepositories(openRepositoryPath)
    WorkbenchMenu.install(scene, openRepositoryPath, true, this)
  }

  override fun onCloseProjectClicked() {
    scene.closeWindow()
    OpenRepoScene.launch(Stage())
  }
}
