package io.redgreen.liftoff.javafx.components

import io.redgreen.liftoff.javafx.components.DiscoverGitReposComboBox.GitRepo
import java.io.File
import javafx.collections.FXCollections
import javafx.scene.control.ComboBox
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.util.Callback

class DiscoverGitReposComboBox(
  directory: File,
  onSelected: (GitRepo) -> Unit
) : ComboBox<GitRepo>() {

  init {
    val gitProjectsCellFactory = gitProjectsCellFactory()
    buttonCell = gitProjectsCellFactory.call(null)
    cellFactory = gitProjectsCellFactory
    items = FXCollections.observableArrayList(naiveGetPossibleGitProjects(directory))

    selectionModel.selectedItemProperty().addListener { _, _, selectedGitProject ->
      onSelected(selectedGitProject)
    }
  }

  private fun naiveGetPossibleGitProjects(
    gitProjectsRoot: File
  ): List<GitRepo> {
    val gitRepos = gitProjectsRoot
      .list()!!
      .map { File("${gitProjectsRoot.absolutePath}/$it/.git") }
      .filter { it.exists() }
      .map(::GitRepo)
      .toMutableList()
    gitRepos.sortBy { it.gitDirectory.absolutePath.toLowerCase() }
    return gitRepos
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
