package redgreen.dawn.affectedfiles.view.javafx

import javafx.collections.ObservableList
import javafx.scene.control.ListView
import javafx.util.Callback
import redgreen.dawn.affectedfiles.model.AffectedFile

internal class AffectedFilesListView(
  affectedFiles: ObservableList<AffectedFile>
) : ListView<AffectedFile>(affectedFiles) {
  init {
    stylesheets.add("/css/affected-files.css")
    cellFactory = Callback { AffectedFileListCells() }
    isEditable = false
  }
}
