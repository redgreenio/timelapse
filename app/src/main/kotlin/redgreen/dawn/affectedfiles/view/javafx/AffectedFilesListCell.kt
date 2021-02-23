package redgreen.dawn.affectedfiles.view.javafx

import java.lang.ref.WeakReference
import javafx.scene.control.ListCell
import javafx.scene.layout.Region
import redgreen.dawn.affectedfiles.view.model.AffectedFilesCellViewModel
import redgreen.dawn.affectedfiles.view.model.AffectedFilesCellViewModel.DirectoryCell
import redgreen.dawn.affectedfiles.view.model.AffectedFilesCellViewModel.FileCell

internal class AffectedFilesListCell : ListCell<AffectedFilesCellViewModel>() {
  private var affectedDirectoryRowReference = WeakReference(AffectedDirectoryRow())
  private var affectedFileRowReference = WeakReference(AffectedFileRow())

  override fun updateItem(cellViewModel: AffectedFilesCellViewModel?, empty: Boolean) {
    super.updateItem(cellViewModel, empty)
    graphic = if (empty || cellViewModel == null) {
      null
    } else {
      cellFor(cellViewModel)
    }
  }

  private fun cellFor(viewModel: AffectedFilesCellViewModel): Region = when (viewModel) {
    is DirectoryCell -> existingOrNewDirectoryRow().apply { setData(viewModel) }
    is FileCell -> existingOrNewFileRow().apply { setData(viewModel) }
  }

  private fun existingOrNewDirectoryRow(): AffectedDirectoryRow =
    affectedDirectoryRowReference.get() ?: AffectedDirectoryRow().apply {
      affectedDirectoryRowReference = WeakReference(this)
    }

  private fun existingOrNewFileRow(): AffectedFileRow =
    affectedFileRowReference.get() ?: AffectedFileRow().apply {
      affectedFileRowReference = WeakReference(this)
    }
}
