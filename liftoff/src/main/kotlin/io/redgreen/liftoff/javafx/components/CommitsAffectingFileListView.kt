package io.redgreen.liftoff.javafx.components

import javafx.collections.FXCollections
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.util.Callback
import kotlin.properties.Delegates
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit

class CommitsAffectingFileListView(
  onSelected: (commitId: String) -> Unit
) : ListView<RevCommit>() {

  class FileModel(
    val repository: Repository,
    val path: String
  )

  init {
    cellFactory = revCommitCellFactory()
    selectionModel.selectedItemProperty().addListener { _, _, revCommit ->
      revCommit ?: return@addListener
      onSelected(revCommit.name)
    }
  }

  private fun revCommitCellFactory(): Callback<ListView<RevCommit>, ListCell<RevCommit>> {
    return Callback<ListView<RevCommit>, ListCell<RevCommit>> {
      return@Callback object : ListCell<RevCommit>() {
        override fun updateItem(revCommit: RevCommit?, empty: Boolean) {
          super.updateItem(revCommit, empty)

          if (revCommit == null || empty) {
            graphic = null
          } else {
            text = displayMessage(revCommit)
          }
        }

        private fun displayMessage(revCommit: RevCommit): String {
          val shortCommitId = revCommit.name.take(8)
          val truncatedMessage = revCommit.shortMessage.take(48)
          val messageToShow = if (truncatedMessage.length != revCommit.shortMessage.length) {
            "$truncatedMessage…"
          } else {
            truncatedMessage
          }
          return "$shortCommitId • $messageToShow"
        }
      }
    }
  }

  var fileModel: FileModel? by Delegates.observable(null) { _, _, fileModel ->
    items = if (fileModel == null) {
      FXCollections.emptyObservableList()
    } else {
      FXCollections.observableArrayList(getCommits(fileModel))
    }
  }

  private fun getCommits(fileModel: FileModel): List<RevCommit> =
    Git(fileModel.repository)
      .log()
      .addPath(fileModel.path)
      .call()
      .toList()
}
