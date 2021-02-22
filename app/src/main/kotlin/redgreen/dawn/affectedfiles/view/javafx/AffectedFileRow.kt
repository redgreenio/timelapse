package redgreen.dawn.affectedfiles.view.javafx

import io.redgreen.timelapse.foo.fastLazy
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.geometry.VPos
import javafx.scene.control.Label
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.RowConstraints
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import redgreen.dawn.affectedfiles.view.model.AffectedFilesCellViewModel.FileCell

internal class AffectedFileRow : GridPane() {
  companion object {
    private const val ROW_HEIGHT = 32.0
    private const val ROW_PADDING = 4.0

    private const val CONTENT_SPACING = 8.0

    private const val FONT_FAMILY = "Roboto"
    private const val FONT_SIZE = 13.0
  }

  // TODO Have one instance of the font?
  private val filePathFont by fastLazy { Font.font(FONT_FAMILY, FontWeight.NORMAL, FONT_SIZE) }

  private val mnemonicTile by fastLazy { MnemonicTile() }
  private val filePathLabel by fastLazy { Label().apply { font = filePathFont; minWidth = USE_PREF_SIZE } }

  init {
    prefHeight = ROW_HEIGHT
    padding = Insets(0.0, ROW_PADDING, 0.0, ROW_PADDING)

    // Constraints
    rowConstraints.add(
      RowConstraints(ROW_HEIGHT, ROW_HEIGHT, ROW_HEIGHT, Priority.NEVER, VPos.CENTER, true)
    )

    columnConstraints.addAll(
      ColumnConstraints(),
      ColumnConstraints(CONTENT_SPACING, CONTENT_SPACING, CONTENT_SPACING, Priority.NEVER, HPos.LEFT, false),
      ColumnConstraints(),
    )

    // Content
    add(mnemonicTile, 0, 0)
    add(Region().apply { prefWidth(CONTENT_SPACING) }, 1, 0)
    add(filePathLabel, 2, 0)
  }

  fun setData(data: FileCell) {
    mnemonicTile.setAffectedFile(data.affectedFile)
    filePathLabel.text = data.affectedFile.filename
  }
}
