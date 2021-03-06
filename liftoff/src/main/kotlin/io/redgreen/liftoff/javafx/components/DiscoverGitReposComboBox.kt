package io.redgreen.liftoff.javafx.components

import io.redgreen.liftoff.javafx.components.DiscoverGitReposComboBox.GitRepo
import java.io.File
import javafx.scene.control.ComboBox
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.util.Callback

// FIXME: 06/03/21 Maybe replace with a regular ComboBox?
class DiscoverGitReposComboBox(
  onSelected: (GitRepo) -> Unit
) : ComboBox<GitRepo>() {

  init {
    val gitProjectsCellFactory = gitProjectsCellFactory()
    buttonCell = gitProjectsCellFactory.call(null)
    cellFactory = gitProjectsCellFactory

    selectionModel.selectedItemProperty().addListener { _, _, selectedGitProject ->
      selectedGitProject ?: return@addListener
      onSelected(selectedGitProject)
    }
  }

  private fun gitProjectsCellFactory(): Callback<ListView<GitRepo>, ListCell<GitRepo>> {
    return Callback<ListView<GitRepo>, ListCell<GitRepo>> {
      return@Callback object : ListCell<GitRepo>() {
        override fun updateItem(gitRepo: GitRepo?, empty: Boolean) {
          super.updateItem(gitRepo, empty)
          if (gitRepo == null || empty) {
            graphic = null
          } else {
            text = gitRepo.parentDirectoryName
          }
        }
      }
    }
  }

  data class GitRepo(val gitDirectory: File) {
    val parentDirectoryName: String =
      gitDirectory.absolutePath.split('/').dropLast(1).takeLast(1).first()
  }
}
