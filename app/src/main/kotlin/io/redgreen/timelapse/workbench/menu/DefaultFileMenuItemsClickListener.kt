package io.redgreen.timelapse.workbench.menu

import io.redgreen.timelapse.foo.appendDotGit
import io.redgreen.timelapse.foo.closeWindow
import io.redgreen.timelapse.git.model.GitDirectory
import io.redgreen.timelapse.main.TimelapseScene
import io.redgreen.timelapse.openrepo.storage.PreferencesRecentGitRepositoriesStorage
import io.redgreen.timelapse.openrepo.view.OPEN_REPOSITORY_TITLE
import io.redgreen.timelapse.openrepo.view.OpenRepoScene
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import java.io.File

class DefaultFileMenuItemsClickListener(
  private val scene: Scene,
  private val openRepositoryPath: String
) : FileMenuItemsClickListener {
  override fun onOpenClicked() {
    val directoryPath = DirectoryChooser()
      .apply { initialDirectory = (File(System.getProperty("user.home"))) }
      .showDialog(scene.window)
    val maybeGitDirectory = GitDirectory.from(directoryPath.canonicalPath.appendDotGit())
    if (maybeGitDirectory.isPresent) {
      scene.closeWindow()
      TimelapseScene.launch(Stage(), directoryPath.canonicalPath.appendDotGit())
    } else {
      Alert(Alert.AlertType.ERROR).apply {
        title = OPEN_REPOSITORY_TITLE
        headerText = "Not a Git repository"
        contentText = "'${directoryPath.canonicalPath}' is not a Git repository."
      }.showAndWait()
    }
  }

  override fun onRecentClicked(repositoryPath: String) {
    scene.closeWindow()
    TimelapseScene.launch(Stage(), repositoryPath)
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
