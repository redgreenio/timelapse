package redgreen.dawn.affectedfiles.view.javafx

import io.redgreen.timelapse.foo.fastLazy
import javafx.geometry.Insets
import javafx.geometry.VPos
import javafx.scene.control.Label
import javafx.scene.control.OverrunStyle.CENTER_ELLIPSIS
import javafx.scene.control.Tooltip
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.RowConstraints
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import redgreen.dawn.affectedfiles.view.model.AffectedFileCellViewModel.DirectoryCell

internal class AffectedDirectoryRow(
  private val listView: AffectedFilesListView
) : GridPane() {
  companion object {
    private const val ROW_HEIGHT = 28.0

    private const val HEX_BACKGROUND = "0xC4C4C4"

    private const val PADDING = 8.0
    private const val GUTTER = 24.0

    private const val FONT_FAMILY = "Roboto Medium"
    private const val FONT_SIZE = 12.0
  }

  private val directoryPathFont by fastLazy { Font.font(FONT_FAMILY, FontWeight.MEDIUM, FONT_SIZE) }

  private val tooltip by fastLazy { Tooltip() }

  private val directoryPathLabel by fastLazy {
    Label().apply {
      font = directoryPathFont
      textOverrun = CENTER_ELLIPSIS
      prefWidthProperty().bind(listView.widthProperty().subtract(GUTTER))
    }
  }

  init {
    prefHeight = ROW_HEIGHT
    background = Background(BackgroundFill(Color.web(HEX_BACKGROUND), CornerRadii.EMPTY, Insets.EMPTY))

    padding = Insets(0.0, PADDING, 0.0, PADDING)

    // Constraints
    rowConstraints.add(
      RowConstraints(ROW_HEIGHT, ROW_HEIGHT, ROW_HEIGHT, Priority.NEVER, VPos.CENTER, true)
    )

    columnConstraints.addAll(
      ColumnConstraints()
    )

    // Content
    add(directoryPathLabel, 0, 0)
  }

  fun setData(data: DirectoryCell) {
    directoryPathLabel.text = data.path
    setupTooltip(data.path)
  }

  private fun setupTooltip(directoryPath: String) {
    Tooltip.uninstall(directoryPathLabel, tooltip)
    Tooltip.install(directoryPathLabel, tooltip)
    tooltip.text = directoryPath
  }
}
