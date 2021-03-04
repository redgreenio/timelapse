package io.redgreen.liftoff.javafx.components

import io.redgreen.liftoff.javafx.components.DiscoverGitReposComboBox.GitRepo
import javafx.collections.FXCollections
import kotlin.properties.Delegates
import org.controlsfx.control.SearchableComboBox
import org.eclipse.jgit.lib.Constants.HEAD
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.lib.RepositoryBuilder
import org.eclipse.jgit.treewalk.TreeWalk

class FilesInRepoComboBox(onSelected: (filePath: String) -> Unit) : SearchableComboBox<String>() {
  var gitRepo: GitRepo? by Delegates.observable(null) { _, _, selectedGitRepo ->
    items = if (selectedGitRepo == null) {
      FXCollections.emptyObservableList()
    } else {
      val repository = RepositoryBuilder()
        .setGitDir(selectedGitRepo.gitDirectory)
        .build()

      FXCollections.observableArrayList(repository.getFilePaths())
    }

    selectionModel.selectedItemProperty().addListener { _, oldPath, newPath ->
      val firstSelection = oldPath == null && newPath != null
      if (firstSelection) {
        onSelected(newPath)
      }
    }
  }

  private fun Repository.getFilePaths(): List<String> {
    val headCommit = this.parseCommit(this.resolve(HEAD))
    val filePaths = mutableListOf<String>()
    TreeWalk(this).use { treeWalk ->
      treeWalk.reset(headCommit.tree.id)
      treeWalk.isRecursive = true
      while (treeWalk.next()) {
        filePaths.add(treeWalk.pathString)
      }
    }
    return filePaths.toList()
  }
}
