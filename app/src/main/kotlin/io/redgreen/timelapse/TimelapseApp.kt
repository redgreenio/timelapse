package io.redgreen.timelapse

import io.redgreen.timelapse.changedfiles.contracts.ReadingAreaContract
import io.redgreen.timelapse.changedfiles.view.ChangedFilesPane
import io.redgreen.timelapse.diff.FormattedDiff
import io.redgreen.timelapse.domain.BlobDiff.Simple
import io.redgreen.timelapse.domain.Change
import io.redgreen.timelapse.domain.getBlobDiff
import io.redgreen.timelapse.domain.getCommitHistoryText
import io.redgreen.timelapse.domain.openGitRepository
import io.redgreen.timelapse.domain.parseGitFollowOutput
import io.redgreen.timelapse.domain.readFileFromCommitId
import io.redgreen.timelapse.fileexplorer.view.FileExplorerPane
import io.redgreen.timelapse.fileexplorer.view.FileExplorerPane.FileSelectionListener
import io.redgreen.timelapse.foo.debug
import io.redgreen.timelapse.foo.fastLazy
import io.redgreen.timelapse.people.view.PeoplePane
import io.redgreen.timelapse.readingarea.CommitInformationPane
import io.redgreen.timelapse.ui.ReadingPane
import io.redgreen.timelapse.vcs.ChangedFile
import io.redgreen.timelapse.vcs.ChangedFile.Addition
import io.redgreen.timelapse.vcs.ChangedFile.Deletion
import io.redgreen.timelapse.vcs.ChangedFile.Modification
import io.redgreen.timelapse.vcs.ChangedFile.Rename
import io.redgreen.timelapse.visuals.AreaChart
import io.redgreen.timelapse.visuals.Commit
import io.sentry.Sentry
import javafx.application.Application
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Slider
import javafx.scene.control.SplitPane
import javafx.scene.input.KeyCode.DIGIT1
import javafx.scene.input.KeyCode.DIGIT2
import javafx.scene.input.KeyCode.DIGIT3
import javafx.scene.input.KeyCode.ESCAPE
import javafx.scene.input.KeyCode.LEFT
import javafx.scene.input.KeyCode.RIGHT
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination.CONTROL_ANY
import javafx.scene.input.KeyEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.RowConstraints
import javafx.scene.layout.VBox
import javafx.stage.Screen
import javafx.stage.Stage
import org.eclipse.jgit.lib.Repository
import java.io.File
import java.time.LocalDate
import kotlin.math.round
import kotlin.system.exitProcess

private const val ALPHA = "\uD835\uDEFC"
private const val APP_NAME = "Timelapse (Pre-$ALPHA)"

private const val AREA_CHART_HEIGHT = 100.0

private const val NO_PADDING = 0.0
private const val PADDING = 10.0

class TimelapseApp : Application(), ReadingAreaContract, FileSelectionListener {
  private lateinit var project: String
  private val gitRepository by lazy { openGitRepository(File(project)) }
  private lateinit var changes: List<Change>
  private lateinit var filePath: String

  private val timelapseSlider by fastLazy {
    Slider().apply {
      min = 0.0
      padding = Insets(PADDING, NO_PADDING, NO_PADDING, NO_PADDING)
      blockIncrement = 1.0
      majorTickUnit = 1.0
      minorTickCount = 0

      valueProperty().addListener { _, _, value ->
        if (!::changes.isInitialized) {
          return@addListener
        }

        val changeIndex = value.toInt()
        val (_, selectedChange) = getChanges(changes, changeIndex)

        // Show code on slider move
        showCode(filePath, selectedChange)

        // Update anchor in area chart
        insertionsAreaChart.setAnchorIndex(changeIndex)

        // Keyboard shortcuts
        val newestCommitKeyCodeCombination = KeyCodeCombination(RIGHT, CONTROL_ANY)
        val oldestCommitKeyCodeCombination = KeyCodeCombination(LEFT, CONTROL_ANY)

        with(scene.accelerators) {
          put(newestCommitKeyCodeCombination) { if (max != 0.0) setValue(max) }
          put(oldestCommitKeyCodeCombination) { if (max != 0.0) setValue(min) }
        }
      }
    }
  }

  private val commitInformationPane by fastLazy { CommitInformationPane(gitRepository) }

  private val insertionsAreaChart by fastLazy {
    AreaChart().apply { prefHeight = AREA_CHART_HEIGHT }
  }

  private val readingPane by fastLazy { ReadingPane(gitRepository) }

  private val sliderPane by fastLazy {
    val sliderAndInformationPane = VBox().apply {
      timelapseSlider.prefWidthProperty().bind(widthProperty())
      children.addAll(timelapseSlider, commitInformationPane)
    }

    BorderPane().apply {
      center = insertionsAreaChart
      bottom = sliderAndInformationPane
    }
  }

  private val centerPane by fastLazy {
    BorderPane().apply {
      top = sliderPane
      center = readingPane
    }
  }

  private fun getTitle(changedFile: ChangedFile): String {
    return when (changedFile) {
      is Addition -> "[New File] ${changedFile.filePath}"
      is Modification -> "[Modified] ${changedFile.filePath}"
      is Deletion -> "[Deleted] ${changedFile.filePath}"
      is Rename -> "[Renamed] ${changedFile.oldFilePath} => ${changedFile.filePath}"
    }
  }

  private val changedFilesPane by fastLazy { ChangedFilesPane(gitRepository, this) }

  private val peoplePane by fastLazy { PeoplePane(gitRepository) }

  private val fileExplorerPane by fastLazy {
    FileExplorerPane(project, gitRepository, this)
  }

  private val rightPane by fastLazy {
    GridPane().apply {
      changedFilesPane.prefWidthProperty().bind(widthProperty())
      peoplePane.prefWidthProperty().bind(widthProperty())

      add(changedFilesPane, 0, 0)
      add(peoplePane, 0, 1)

      with(rowConstraints) {
        add(RowConstraints().apply { percentHeight = 50.0 }) // Row 1 (Changed Files)
        add(RowConstraints().apply { percentHeight = 50.0 }) // Row 2 (People)
      }
    }
  }

  override fun start(primaryStage: Stage) {
    project = parameters.raw.first()
    debug { "Project: $project" }

    val rootPane = SplitPane().apply {
      items.addAll(fileExplorerPane, centerPane, rightPane)
      setDividerPositions(0.18, 0.82)
    }

    with(primaryStage) {
      val screenBounds = Screen.getPrimary().bounds
      scene = Scene(rootPane, screenBounds.width, screenBounds.height)
      title = APP_NAME
      isMaximized = true
      show()

      setupHotKeys(scene)

      setOnCloseRequest {
        // FIXME: 19-11-2020 The application does not exit due to running thread pool executors? Shut down Mobius loops?
        Platform.exit()
        exitProcess(0)
      }
    }
  }

  private fun moveFocusToReadingPane() {
    if (readingPane.isShowingOverlap()) {
      readingPane.focusOnOverlap()
    } else {
      timelapseSlider.requestFocus()
    }
  }

  override fun onFilePathSelected(filePath: String, startDateEndDate: Pair<LocalDate, LocalDate>?) {
    this.filePath = filePath

    // Get change history
    val gitFollowOutput = getCommitHistoryText(project, filePath, startDateEndDate)
    changes = parseGitFollowOutput(gitFollowOutput)
      .reversed()

    debug { "Found ${changes.size} commits for $filePath" }

    // Pair area chart with insertions
    with(insertionsAreaChart) {
      commits = changes
        .map { Commit(it.insertions, it.deletions) }
    }

    // Pair slider with change history
    with(timelapseSlider) {
      max = changes.lastIndex.toDouble()
      value = 0.0
      debug { "Setting slider's maximum to $max, value to $value" }
    }

    // Show code now
    val (_, selectedChange) = getChanges(changes, 0)
    showCode(filePath, selectedChange)
  }

  private fun getChanges(
    changes: List<Change>,
    changeIndex: Int
  ): Pair<Change?, Change> {
    val previousChange = if (changeIndex == 0) null else changes[changeIndex - 1]
    val selectedChange = changes[changeIndex]
    return Pair(previousChange, selectedChange)
  }

  private fun showCode(
    filePath: String,
    selectedChange: Change
  ) {
    readingPane.showMainDiff(filePath, selectedChange.commitId)

    Platform.runLater {
      commitInformationPane.showCommitInformation(
        selectedChange,
        round(timelapseSlider.value + 1).toInt(),
        changes.size
      )
    }

    changedFilesPane.selectFileAndRevision(filePath, selectedChange.commitId)
    peoplePane.selectFileAndRevision(filePath, selectedChange.commitId)
  }

  private fun Repository.getChangeText(
    filePath: String,
    commitId: String
  ): String {
    return readFileFromCommitId(commitId, filePath)
  }

  override fun showChangedFileDiff(commitId: String, changedFile: ChangedFile) {
    val diff = FormattedDiff.from((gitRepository.getBlobDiff(commitId, changedFile.filePath) as Simple).rawDiff)
    readingPane.showOverlappingDiff(getTitle(changedFile), diff)
  }

  private fun setupHotKeys(scene: Scene) {
    with(scene) {
      accelerators[KeyCodeCombination(DIGIT1, CONTROL_ANY)] = Runnable { fileExplorerPane.focus() }
      accelerators[KeyCodeCombination(DIGIT2, CONTROL_ANY)] = Runnable { moveFocusToReadingPane() }
      accelerators[KeyCodeCombination(DIGIT3, CONTROL_ANY)] = Runnable { changedFilesPane.focusOnList() }
    }

    scene.addEventHandler(KeyEvent.KEY_RELEASED) { event ->
      if (event.code == ESCAPE) {
        readingPane.dismissOverlap()
        timelapseSlider.requestFocus()
      }
    }
  }

  override fun init() {
    super.init()
    Sentry.init { options ->
      options.dsn = "https://9ccbeb20047c42e49a9fbe6094cd9896@o483785.ingest.sentry.io/5536123"
    }
  }
}
