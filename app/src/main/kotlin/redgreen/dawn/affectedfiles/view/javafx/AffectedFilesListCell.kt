package redgreen.dawn.affectedfiles.view.javafx

import io.redgreen.timelapse.foo.fastLazy
import javafx.scene.control.ListCell
import javafx.scene.layout.Region
import redgreen.dawn.affectedfiles.view.model.AffectedFilesCellViewModel
import redgreen.dawn.affectedfiles.view.model.AffectedFilesCellViewModel.DirectoryCell
import redgreen.dawn.affectedfiles.view.model.AffectedFilesCellViewModel.FileCell

internal class AffectedFilesListCell : ListCell<AffectedFilesCellViewModel>() {
  private val affectedDirectoryRow by fastLazy { AffectedDirectoryRow() }
  private val affectedFileRow by fastLazy { AffectedFileRow() }

  override fun updateItem(cellViewModel: AffectedFilesCellViewModel?, empty: Boolean) {
    super.updateItem(cellViewModel, empty)
    graphic = if (empty || cellViewModel == null) {
      null
    } else {
      cellFor(cellViewModel)
    }
  }

  private fun cellFor(cellViewModel: AffectedFilesCellViewModel): Region =
    when (cellViewModel) {
      is DirectoryCell -> {
        affectedDirectoryRow.setData(cellViewModel)
        affectedDirectoryRow
      }

      is FileCell -> {
        affectedFileRow.
        setData(cellViewModel)
        affectedFileRow
      }
    }
}
