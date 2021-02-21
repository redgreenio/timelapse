package redgreen.dawn.affectedfiles.view.javafx

import io.redgreen.timelapse.foo.fastLazy
import javafx.scene.control.ListCell
import redgreen.dawn.affectedfiles.model.AffectedFile

internal class AffectedFileListCells : ListCell<AffectedFile>() {
  private val affectedFileRow by fastLazy { AffectedFileRow() }

  override fun updateItem(affectedFile: AffectedFile?, empty: Boolean) {
    super.updateItem(affectedFile, empty)
    graphic = if (empty || affectedFile == null) {
      null
    } else {
      affectedFileRow.setAffectedFile(affectedFile)
      affectedFileRow
    }
  }
}
