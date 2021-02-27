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
import redgreen.dawn.affectedfiles.view.model.AffectedFileCellViewModel
import redgreen.dawn.affectedfiles.view.model.AffectedFileCellViewModel.DirectoryCell
import redgreen.dawn.affectedfiles.view.model.summarize

internal class AffectedDirectoryRow(
  private val listView: AffectedFilesListView
) : GridPane() {
  companion object {
    private const val ROW_HEIGHT = 28.0

    private const val FILE_PATH_FONT_FAMILY = "Roboto Medium"
    private const val FONT_SIZE = 12.0
    private const val HEX_ROW_BACKGROUND = "0xC4C4C4"
    private const val ZERO_PADDING = 0.0
    private const val PADDING = 8.0
    private const val GUTTER = 38.0

    private const val FILE_COUNT_FONT_FAMILY = "Roboto Black"
    private const val HEX_FILE_COUNT_FOREGROUND = "0xEFEFEF"
    private const val HEX_FILE_COUNT_BACKGROUND = "0x8F8F8F"
    private const val CORNER_RADIUS = 1.5
    private const val FILE_COUNT_VERTICAL_PADDING = 2.0
    private const val FILE_COUNT_HORIZONTAL_PADDING = 6.0
  }

  private val directoryPathFont by fastLazy { Font.font(FILE_PATH_FONT_FAMILY, FontWeight.MEDIUM, FONT_SIZE) }
  private val directoryPathTooltip by fastLazy { Tooltip() }
  private val directoryPathLabel by fastLazy {
    Label().apply {
      font = directoryPathFont
      textOverrun = CENTER_ELLIPSIS
      padding = Insets(ZERO_PADDING, PADDING, ZERO_PADDING, ZERO_PADDING)
      prefWidthProperty().bind(listView.widthProperty().subtract(GUTTER))
    }
  }

  private val fileCountFont by fastLazy { Font.font(FILE_COUNT_FONT_FAMILY, FontWeight.NORMAL, FONT_SIZE) }
  private val fileCountTooltip by fastLazy { Tooltip() }
  private val fileCountLabel by fastLazy {
    Label().apply {
      font = fileCountFont
      textFill = Color.web(HEX_FILE_COUNT_FOREGROUND)
      padding = Insets(
        FILE_COUNT_VERTICAL_PADDING,
        FILE_COUNT_HORIZONTAL_PADDING,
        FILE_COUNT_VERTICAL_PADDING,
        FILE_COUNT_HORIZONTAL_PADDING
      )
      background = Background(BackgroundFill(Color.web(HEX_FILE_COUNT_BACKGROUND), CornerRadii(CORNER_RADIUS), Insets.EMPTY))
    }
  }

  init {
    prefHeight = ROW_HEIGHT
    background = Background(BackgroundFill(Color.web(HEX_ROW_BACKGROUND), CornerRadii.EMPTY, Insets.EMPTY))

    padding = Insets(ZERO_PADDING, PADDING, ZERO_PADDING, PADDING)

    // Constraints
    rowConstraints.add(
      RowConstraints(ROW_HEIGHT, ROW_HEIGHT, ROW_HEIGHT, Priority.NEVER, VPos.CENTER, true)
    )

    columnConstraints.addAll(
      ColumnConstraints(),
      ColumnConstraints()
    )

    // Content
    add(directoryPathLabel, 0, 0)
    add(fileCountLabel, 1, 0)
  }

  fun setData(data: DirectoryCell) {
    directoryPathLabel.text = data.path
    setupDirectoryPathTooltip(data.path)

    fileCountLabel.text = data.fileCount.toString()

    val directoryCellIndex = listView.items.indexOf(data)
    val affectedFiles = listView.items
      .slice(IntRange(directoryCellIndex + 1, directoryCellIndex + data.fileCount))
    setupFileCountTooltip(affectedFiles)
  }

  private fun setupDirectoryPathTooltip(directoryPath: String) {
    Tooltip.uninstall(directoryPathLabel, directoryPathTooltip)
    Tooltip.install(directoryPathLabel, directoryPathTooltip)
    directoryPathTooltip.text = directoryPath
  }

  private fun setupFileCountTooltip(
    affectedFiles: List<AffectedFileCellViewModel>
  ) {
    Tooltip.uninstall(fileCountLabel, fileCountTooltip)
    Tooltip.install(fileCountLabel, fileCountTooltip)
    fileCountTooltip.text = affectedFiles.summarize()
  }
}
