package io.redgreen.liftoff.javafx.components

import io.redgreen.timelapse.git.model.GitDirectory
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.util.Callback
import org.controlsfx.control.SearchableComboBox

// FIXME: 06/03/21 Maybe replace with a regular ComboBox?
class DiscoverGitDirectoriesComboBox(
  onSelected: (GitDirectory) -> Unit
) : SearchableComboBox<GitDirectory>() {

  init {
    val gitProjectsCellFactory = gitProjectsCellFactory()
    buttonCell = gitProjectsCellFactory.call(null)
    cellFactory = gitProjectsCellFactory

    selectionModel.selectedItemProperty().addListener { _, _, selectedGitProject ->
      selectedGitProject ?: return@addListener
      onSelected(selectedGitProject)
    }
  }

  private fun gitProjectsCellFactory(): Callback<ListView<GitDirectory>, ListCell<GitDirectory>> {
    return Callback<ListView<GitDirectory>, ListCell<GitDirectory>> {
      return@Callback object : ListCell<GitDirectory>() {
        override fun updateItem(gitDirectory: GitDirectory?, empty: Boolean) {
          super.updateItem(gitDirectory, empty)
          if (gitDirectory == null || empty) {
            graphic = null
          } else {
            text = bestGuessProjectName(gitDirectory.path)
          }
        }

        private fun bestGuessProjectName(gitDirectoryPath: String): String =
          gitDirectoryPath.split('/').dropLast(1).takeLast(1).first()
      }
    }
  }
}
