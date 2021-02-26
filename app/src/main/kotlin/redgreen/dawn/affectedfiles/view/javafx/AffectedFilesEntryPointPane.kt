package redgreen.dawn.affectedfiles.view.javafx

import io.reactivex.rxjava3.disposables.Disposable
import javafx.collections.FXCollections
import javafx.scene.layout.Pane
import redgreen.dawn.affectedfiles.contract.AffectedFilesProps
import redgreen.dawn.affectedfiles.view.model.AffectedFileCellViewModel.FileCell
import redgreen.dawn.architecture.Disposer
import redgreen.dawn.architecture.EntryPoint
import redgreen.dawn.architecture.RxJava3Disposer
import redgreen.dawn.extentions.matchParent

class AffectedFilesEntryPointPane : Pane(),
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
    children.add(affectedFilesListView)

    with(props) {
      viewModelChanges.subscribe { affectedFileCellViewModels ->
        affectedFilesListView.items = FXCollections.observableList(affectedFileCellViewModels)
      }.collect()
      contextChanges.subscribe { println(it) }.collect()
    }
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
