package redgreen.dawn.affectedfiles.view.javafx

import io.reactivex.rxjava3.disposables.Disposable
import io.redgreen.architecture.Disposer
import io.redgreen.architecture.EntryPoint
import io.redgreen.architecture.RxJava3Disposer
import io.redgreen.design.TitledParent
import redgreen.dawn.affectedfiles.contract.AffectedFilesProps
import redgreen.dawn.affectedfiles.view.model.AffectedFileCellViewModel.FileCell
import redgreen.dawn.extentions.matchParent

class AffectedFilesEntryPointPane : TitledParent(),
  EntryPoint<AffectedFilesProps>,
  Disposer<Disposable> by RxJava3Disposer() {

  override fun mount(props: AffectedFilesProps) {
    val parent = this
    val affectedFilesListView = AffectedFilesListView().apply {
      matchParent(parent)
      selectionModel.selectedItemProperty().addListener { _, oldValue, newValue ->
        if (newValue != oldValue && newValue is FileCell) {
          props.fileSelectedCallback(newValue.affectedFile.filePath)
        } else {
          skipDirectoryRow(items.indexOf(oldValue), items.indexOf(newValue))
        }
      }
    }

    setContent("Affected files (none)", affectedFilesListView)

    props.contextChanges.subscribe { println(it) }.collect()
  }

  private fun AffectedFilesListView.skipDirectoryRow(
    previousSelectionIndex: Int,
    currentSelectionIndex: Int
  ) {
    when {
      currentSelectionIndex > previousSelectionIndex -> selectionModel.selectNext()
      currentSelectionIndex > 1 -> selectionModel.selectPrevious()

      /* prevent moving the selection to index 0, which is always a directory row */
      else -> selectionModel.selectNext()
    }
  }

  override fun unmount() {
    dispose()
  }
}
