package io.redgreen.timelapse.affectedfiles.view.javafx

import io.redgreen.timelapse.affectedfiles.view.model.AffectedFileCellViewModel
import io.redgreen.timelapse.affectedfiles.view.model.AffectedFileCellViewModel.DirectoryCell
import io.redgreen.timelapse.affectedfiles.view.model.summarize
import io.redgreen.timelapse.foo.fastLazy
import javafx.geometry.VPos
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.RowConstraints

internal class AffectedDirectoryRow(
  private val listView: AffectedFilesListView
) : GridPane() {
  companion object {
    private const val CSS_FILE = "/css/affected-files/affected-directory-row.css"

    private const val CSS_CLASS_DIRECTORY_ROW = "directory-row"
    private const val CSS_CLASS_DIRECTORY_PATH = "directory-path"
    private const val CSS_CLASS_FILE_COUNT = "file-count"

    /**
     * This value should also be changed in the associated CSS file. This value used for row constraints can't be set
     * from CSS (only from FXML).
     */
    private const val ROW_HEIGHT = 28.0
    private const val DIRECTORY_PATH_LABEL_GUTTER = 38.0
  }

  private val directoryPathLabel by fastLazy {
    Label().apply {
      styleClass.add(CSS_CLASS_DIRECTORY_PATH)
      prefWidthProperty().bind(listView.widthProperty().subtract(DIRECTORY_PATH_LABEL_GUTTER))
    }
  }

  private val directoryPathTooltip by fastLazy { Tooltip() }

  private val fileCountLabel by fastLazy {
    Label().apply { styleClass.add(CSS_CLASS_FILE_COUNT) }
  }

  private val fileCountTooltip by fastLazy { Tooltip() }

  init {
    // CSS
    styleClass.add(CSS_CLASS_DIRECTORY_ROW)
    stylesheets.add(CSS_FILE)
    addContent()
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

  private fun addContent() {
    // Constraints
    rowConstraints.add(
      RowConstraints(ROW_HEIGHT, ROW_HEIGHT, ROW_HEIGHT, Priority.NEVER, VPos.CENTER, true)
    )

    // Content
    add(directoryPathLabel, 0, 0)
    add(fileCountLabel, 1, 0)
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
