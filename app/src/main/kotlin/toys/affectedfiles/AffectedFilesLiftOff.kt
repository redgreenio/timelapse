package toys.affectedfiles

import io.reactivex.rxjava3.subjects.PublishSubject
import io.redgreen.liftoff.LiftOff
import io.redgreen.liftoff.javafx.components.DiscoverGitReposComboBox
import java.io.File
import javafx.application.Application
import javafx.geometry.Dimension2D
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import redgreen.dawn.affectedfiles.contract.AffectedFileContext
import redgreen.dawn.affectedfiles.contract.AffectedFilesProps
import redgreen.dawn.affectedfiles.model.AffectedFile.Deleted
import redgreen.dawn.affectedfiles.model.AffectedFile.Modified
import redgreen.dawn.affectedfiles.model.AffectedFile.Moved
import redgreen.dawn.affectedfiles.model.AffectedFile.New
import redgreen.dawn.affectedfiles.view.javafx.AffectedFilesEntryPointPane
import redgreen.dawn.affectedfiles.view.model.AffectedFileCellViewModel
import redgreen.dawn.affectedfiles.view.model.AffectedFileCellViewModel.DirectoryCell
import redgreen.dawn.affectedfiles.view.model.AffectedFileCellViewModel.FileCell

fun main() {
  Application.launch(AffectedFilesLiftOff::class.java)
}

class AffectedFilesLiftOff : LiftOff<AffectedFilesProps, AffectedFilesEntryPointPane>() {
  private companion object {
    private const val TITLE = "Affected files"
    private const val WIDTH = 500.0
    private const val HEIGHT = 600.0

    private const val GIT_PROJECTS_ROOT = "/Users/ragunathjawahar/GitHubProjects/"
  }

  private val contextChanges = PublishSubject.create<AffectedFileContext>()
  private val affectedFilesCellViewModelChanges = PublishSubject
    .create<List<AffectedFileCellViewModel>>()

  private val callbackLabel = Label().apply { isWrapText = true }

  override fun title(): String = TITLE

  override fun howBig(): Dimension2D =
    Dimension2D(WIDTH, HEIGHT)

  override fun entryPoint(): AffectedFilesEntryPointPane =
    AffectedFilesEntryPointPane()

  override fun props(): AffectedFilesProps {
    return AffectedFilesProps(contextChanges, affectedFilesCellViewModelChanges) {
      callbackLabel.text = "Selected file: $it"
    }
  }

  override fun propsUi(): Region {
    val sample1Button = Button("Pre-sorted").apply {
      setOnAction { showSample(sample1ViewModels) }
    }
    val sample2Button = Button("Empty").apply {
      setOnAction { showSample(sample2ViewModels) }
    }
    val gitReposComboBox = DiscoverGitReposComboBox(File(GIT_PROJECTS_ROOT)) {
      println("Selected: ${it.parentDirectoryName}")
    }

    return VBox().apply {
      spacing = 8.0
      children.addAll(
        sample1Button,
        sample2Button,
        callbackLabel,
        gitReposComboBox
      )
    }
  }

  private fun showSample(viewModels: List<AffectedFileCellViewModel>) {
    affectedFilesCellViewModelChanges.onNext(viewModels)
    callbackLabel.text = null
  }
}

private val sample1ViewModels = listOf(
  DirectoryCell("guava/src/com/google/common/collect/", 9),
  FileCell(Modified("guava/src/com/google/common/collect/Collections2.java", 0, 443)),
  FileCell(Modified("guava/src/com/google/common/collect/Multimaps.java", 4, 0)),
  FileCell(Modified("guava/src/com/google/common/collect/Table.java", 3, 0)),
  FileCell(Modified("guava/src/com/google/common/collect/ForwardingTable.java", 2, 0)),
  FileCell(Modified("guava/src/com/google/common/collect/HashBasedTable.java", 2, 0)),
  FileCell(Modified("guava/src/com/google/common/collect/ImmutableTable.java", 2, 0)),
  FileCell(Modified("guava/src/com/google/common/collect/Maps.java", 1, 0)),
  FileCell(Moved("guava/src/com/google/common/collect/SortedTable.java", 1, 1)),
  FileCell(Deleted("guava/src/com/google/common/collect/SortedMaps.java", 374)),

  DirectoryCell("guava/src/com/google/common/cache/", 1),
  FileCell(Modified("guava/src/com/google/common/cache/CacheBuilder.java", 0, 25)),

  DirectoryCell("guava-tests/test/com/google/common/collect/", 2),
  FileCell(Modified("guava-tests/test/com/google/common/collect/Collections2Test.java", 0, 279)),
  FileCell(Deleted("guava-tests/test/com/google/common/collect/SortedMapsTest.java", 222)),

  DirectoryCell("guava-tests/test/com/google/common/cache/", 1),
  FileCell(New("guava-tests/test/com/google/common/cache/CacheBuilderSpecTest.java", 533)),

  DirectoryCell("guava-gwt/src-super/com/google/common/collect/super/com/google/common/collect/", 2),
  FileCell(Modified("guava-gwt/src-super/com/google/common/collect/super/com/google/common/collect/Multimaps.java", 4, 11)),
  FileCell(Modified("guava-gwt/src-super/com/google/common/collect/super/com/google/common/collect/Maps.java", 1, 0)),
)

private val sample2ViewModels = emptyList<AffectedFileCellViewModel>()
