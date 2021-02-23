package redgreen.dawn.affectedfiles.view.javafx

import io.reactivex.rxjava3.disposables.Disposable
import javafx.collections.FXCollections
import javafx.scene.layout.Pane
import redgreen.dawn.affectedfiles.contract.AffectedFilesProps
import redgreen.dawn.affectedfiles.model.AffectedFile.Deleted
import redgreen.dawn.affectedfiles.model.AffectedFile.Modified
import redgreen.dawn.affectedfiles.model.AffectedFile.Moved
import redgreen.dawn.affectedfiles.model.AffectedFile.New
import redgreen.dawn.affectedfiles.view.model.AffectedFileCellViewModel.DirectoryCell
import redgreen.dawn.affectedfiles.view.model.AffectedFileCellViewModel.FileCell
import redgreen.dawn.architecture.Disposer
import redgreen.dawn.architecture.EntryPoint
import redgreen.dawn.architecture.RxJava3Disposer

class AffectedFilesEntryPointPane : Pane(),
  EntryPoint<AffectedFilesProps>,
  Disposer<Disposable> by RxJava3Disposer() {

  override fun mount(props: AffectedFilesProps) {
    val parent = this
    val affectedFilesListView = AffectedFilesListView().apply {
      items = FXCollections.observableList(affectedFilesViewModels)
      prefWidthProperty().bind(parent.widthProperty())
      prefHeightProperty().bind(parent.heightProperty())
    }
    children.add(affectedFilesListView)

    props.contextChanges.subscribe { println(it) }.collect()
  }

  override fun unmount() {
    dispose()
  }
}

// TODO: 24/02/21 Remove this and replace with actual logic
private val affectedFilesViewModels = listOf(
  DirectoryCell("guava/src/com/google/common/collect/"),
  FileCell(Modified("Collections2.java", 0, 443)),
  FileCell(Modified("Multimaps.java", 4, 0)),
  FileCell(Modified("Table.java", 3, 0)),
  FileCell(Modified("ForwardingTable.java", 2, 0)),
  FileCell(Modified("HashBasedTable.java", 2, 0)),
  FileCell(Modified("ImmutableTable.java", 2, 0)),
  FileCell(Modified("Maps.java", 1, 0)),
  FileCell(Moved("SortedTable.java", 1, 1)),
  FileCell(Deleted("SortedMaps.java", 374)),

  DirectoryCell("guava/src/com/google/common/cache/"),
  FileCell(Modified("CacheBuilder.java", 0, 25)),

  DirectoryCell("guava-tests/test/com/google/common/collect/"),
  FileCell(Modified("Collections2Test.java", 0, 279)),
  FileCell(Deleted("SortedMapsTest.java", 222)),

  DirectoryCell("guava-tests/test/com/google/common/cache/"),
  FileCell(New("CacheBuilderSpecTest.java", 533)),

  DirectoryCell("guava-gwt/src-super/com/google/common/collect/super/com/google/common/collect/"),
  FileCell(Modified("Multimaps.java", 4, 11)),
  FileCell(Modified("Maps.java", 1, 0)),
)
