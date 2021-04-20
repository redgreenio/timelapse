package io.redgreen.liftoff.javafx.components

import org.controlsfx.control.SearchableComboBox

class FilesInRepoComboBox(onSelected: (filePath: String) -> Unit) : SearchableComboBox<String>() {
  init {
    selectionModel.selectedItemProperty().addListener { _, oldPath, newPath ->
      val firstSelection = oldPath == null && newPath != null
      if (firstSelection) {
        onSelected(newPath)
      }
    }
  }
}
