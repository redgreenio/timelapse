package redgreen.dawn.affectedfiles.view.javafx

import javafx.collections.ObservableList
import javafx.scene.control.ListView
import javafx.util.Callback
import redgreen.dawn.affectedfiles.view.model.AffectedFilesCellViewModel

internal class AffectedFilesListView(
  affectedFiles: ObservableList<AffectedFilesCellViewModel>
) : ListView<AffectedFilesCellViewModel>(affectedFiles) {
  init {
    stylesheets.add("/css/affected-files.css")
    cellFactory = Callback { AffectedFilesListCell() }
    isEditable = false
  }
}
