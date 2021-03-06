package io.redgreen.liftoff.javafx.components

import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.util.Callback

class FileCommitsListView(
  onSelected: (commitId: String, shortMessage: String) -> Unit
) : ListView<Pair<String, String>>() {
  private companion object {
    private const val COMMIT_ID_LENGTH = 8
    private const val SHORT_MESSAGE_LENGTH = 64
  }

  init {
    cellFactory = commitCellFactory()
    selectionModel.selectedItemProperty().addListener { _, _, commitIsShortMessage ->
      commitIsShortMessage ?: return@addListener
      onSelected(commitIsShortMessage.first, commitIsShortMessage.second)
    }
  }

  private fun commitCellFactory(): Callback<ListView<Pair<String, String>>, ListCell<Pair<String, String>>> {
    return Callback<ListView<Pair<String, String>>, ListCell<Pair<String, String>>> {
      return@Callback object : ListCell<Pair<String, String>>() {
        override fun updateItem(commitIdShortMessage: Pair<String, String>?, empty: Boolean) {
          super.updateItem(commitIdShortMessage, empty)

          text = if (commitIdShortMessage == null || empty) {
            null
          } else {
            displayMessage(commitIdShortMessage)
          }
        }

        private fun displayMessage(commitIdShortMessage: Pair<String, String>): String {
          val (commitId, shortMessage) = commitIdShortMessage
          val shortCommitId = commitId.take(COMMIT_ID_LENGTH)
          val truncatedMessage = shortMessage.take(SHORT_MESSAGE_LENGTH)
          val messageToShow = if (truncatedMessage.length != shortMessage.length) {
            "$truncatedMessage…"
          } else {
            truncatedMessage
          }
          return "$shortCommitId • $messageToShow"
        }
      }
    }
  }
}
