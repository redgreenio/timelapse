package io.redgreen.timelapse.affectedfiles.view.javafx

import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Deleted
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Modified
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Moved
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.New
import io.redgreen.timelapse.affectedfiles.view.model.AffectedFileCellViewModel.FileCell
import io.redgreen.timelapse.foo.fastLazy
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.control.Label
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.RowConstraints

internal class AffectedFileRow : GridPane() {
  companion object {
    private const val CSS_FILE = "/css/affected-files/affected-file-row.css"

    private const val CSS_CLASS_FILE_ROW = "file-row"
    private const val CSS_CLASS_SPACER = "spacer"
    private const val CSS_CLASS_FILENAME = "filename"

    /**
     * This value should also be changed in the associated CSS file. This value used for row constraints can't be set
     * from CSS (only from FXML).
     */
    private const val ROW_HEIGHT = 28.0
  }

  private val mnemonicTile by fastLazy { MnemonicTile() }
  private val filenameLabel by fastLazy { Label().apply { styleClass.add(CSS_CLASS_FILENAME) } }
  private val linesChangedIndicators by fastLazy { LinesChangedIndicators() }

  init {
    styleClass.add(CSS_CLASS_FILE_ROW)
    stylesheets.add(CSS_FILE)
    addContent()
  }

  fun setData(data: FileCell) {
    val affectedFile = data.affectedFile

    mnemonicTile.setAffectedFile(affectedFile)
    filenameLabel.text = affectedFile.filename
    val (deletions, insertions) = when (affectedFile) {
      is New -> 0 to affectedFile.insertions
      is Modified -> affectedFile.deletions to affectedFile.insertions
      is Moved -> affectedFile.deletions to affectedFile.insertions
      is Deleted -> affectedFile.deletions to 0
    }

    linesChangedIndicators.linesChanged(deletions, insertions)
  }

  private fun addContent() {
    // Constraints
    rowConstraints.add(
      RowConstraints(ROW_HEIGHT, ROW_HEIGHT, ROW_HEIGHT, Priority.NEVER, VPos.CENTER, true)
    )

    columnConstraints.addAll(
      ColumnConstraints(),
      ColumnConstraints(),
      ColumnConstraints(),
      ColumnConstraints(-1.0, -1.0, -1.0, Priority.ALWAYS, HPos.RIGHT, true),
    )

    // Content
    add(mnemonicTile, 0, 0)
    add(Region().apply { styleClass.add(CSS_CLASS_SPACER) }, 1, 0)
    add(filenameLabel, 2, 0)
    add(linesChangedIndicators, 3, 0)
  }
}
