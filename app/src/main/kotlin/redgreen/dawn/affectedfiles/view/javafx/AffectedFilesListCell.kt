package redgreen.dawn.affectedfiles.view.javafx

import java.lang.ref.WeakReference
import javafx.scene.control.ListCell
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Region
import redgreen.dawn.affectedfiles.view.model.AffectedFileCellViewModel
import redgreen.dawn.affectedfiles.view.model.AffectedFileCellViewModel.DirectoryCell
import redgreen.dawn.affectedfiles.view.model.AffectedFileCellViewModel.FileCell

internal class AffectedFilesListCell(
  private val listView: AffectedFilesListView
) : ListCell<AffectedFileCellViewModel>() {
  private var affectedDirectoryRowReference = WeakReference(AffectedDirectoryRow(listView))
  private var affectedFileRowReference = WeakReference(AffectedFileRow())

  override fun updateItem(cellViewModel: AffectedFileCellViewModel?, empty: Boolean) {
    super.updateItem(cellViewModel, empty)
    graphic = if (empty || cellViewModel == null) {
      null
    } else {
      cellFor(cellViewModel)
    }
  }

  private fun cellFor(viewModel: AffectedFileCellViewModel): Region = when (viewModel) {
    is DirectoryCell -> existingOrNewDirectoryRow().apply {
      setData(viewModel)
      addEventFilter(MouseEvent.MOUSE_PRESSED) {
        /* because we don't want directory rows to be clickable :) */
        it.consume()
      }
    }
    is FileCell -> existingOrNewFileRow().apply { setData(viewModel) }
  }

  private fun existingOrNewDirectoryRow(): AffectedDirectoryRow =
    affectedDirectoryRowReference.get() ?: AffectedDirectoryRow(listView).apply {
      affectedDirectoryRowReference = WeakReference(this)
    }

  private fun existingOrNewFileRow(): AffectedFileRow =
    affectedFileRowReference.get() ?: AffectedFileRow().apply {
      affectedFileRowReference = WeakReference(this)
    }
}
