package io.redgreen.timelapse

import io.redgreen.timelapse.changedfiles.contracts.ReadingAreaContract
import io.redgreen.timelapse.changedfiles.view.ChangedFilesPane
import io.redgreen.timelapse.diff.DiffLine.Insertion
import io.redgreen.timelapse.diff.FormattedDiff
import io.redgreen.timelapse.domain.Change
import io.redgreen.timelapse.domain.Commit
import io.redgreen.timelapse.domain.getCommitHistoryText
import io.redgreen.timelapse.domain.getDiff
import io.redgreen.timelapse.domain.openGitRepository
import io.redgreen.timelapse.domain.parseGitFollowOutput
import io.redgreen.timelapse.domain.readFileFromCommitId
import io.redgreen.timelapse.fileexplorer.view.FileExplorerPane
import io.redgreen.timelapse.fileexplorer.view.FileExplorerPane.FileSelectionListener
import io.redgreen.timelapse.people.view.PeoplePane
import io.redgreen.timelapse.readingarea.CommitInformationPane
import io.redgreen.timelapse.ui.ReadingPane
import io.redgreen.timelapse.vcs.ChangedFile
import io.redgreen.timelapse.vcs.ChangedFile.Addition
import io.redgreen.timelapse.vcs.ChangedFile.Deletion
import io.redgreen.timelapse.vcs.ChangedFile.Modification
import io.redgreen.timelapse.vcs.ChangedFile.Rename
import io.redgreen.timelapse.visuals.AreaChart
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Slider
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.scene.layout.RowConstraints
import org.eclipse.jgit.lib.Repository
import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.EAST
import java.awt.BorderLayout.NORTH
import java.awt.BorderLayout.SOUTH
import java.awt.BorderLayout.WEST
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.HORIZONTAL
import java.awt.GridBagConstraints.REMAINDER
import java.awt.GridBagLayout
import java.awt.KeyboardFocusManager
import java.awt.event.KeyEvent.VK_1
import java.awt.event.KeyEvent.VK_2
import java.awt.event.KeyEvent.VK_3
import java.awt.event.KeyEvent.VK_ESCAPE
import java.io.File
import java.time.LocalDate
import javax.swing.BorderFactory
import javax.swing.JFrame
import javax.swing.JFrame.EXIT_ON_CLOSE
import javax.swing.JFrame.MAXIMIZED_BOTH
import javax.swing.JPanel
import kotlin.LazyThreadSafetyMode.NONE
import kotlin.math.round

private const val APP_NAME = "Timelapse"

private const val WIDTH = 1024
private const val HEIGHT = 768
private const val AREA_CHART_HEIGHT = 100
private const val FILE_EXPLORER_WIDTH = 320
private const val CHANGES_WIDTH = 400
private const val MATCH_PARENT = 0

private const val NO_PADDING = 0.0
private const val PADDING = 10.0

class TimelapseApp(private val project: String) : Runnable, ReadingAreaContract, FileSelectionListener {
  private val gitRepository by lazy { openGitRepository(File(project)) }
  private lateinit var changes: List<Change>
  private lateinit var filePath: String

  private val timelapseSlider by lazy(NONE) {
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
        val (previousChange, selectedChange) = getChanges(changes, changeIndex)

        // Show code on slider move
        showCode(filePath, previousChange, selectedChange)

        // Update anchor in area chart
        insertionsAreaChart.setAnchorIndex(changeIndex)
      }
    }
  }

  private val commitInformationPane by lazy(NONE) { CommitInformationPane(gitRepository) }

  private val insertionsAreaChart by lazy(NONE) {
    AreaChart().apply { preferredSize = Dimension(WIDTH, AREA_CHART_HEIGHT) }
  }

  private val readingPane = ReadingPane()

  private val sliderPanel by lazy(NONE) {  // TODO: 07-11-2020 create a `fastLazy` extension function
    val sliderAndInformationPanel = JPanel(GridBagLayout()).apply {
      val constraints = GridBagConstraints().apply {
        weightx = 1.0
        fill = HORIZONTAL
        gridwidth = REMAINDER
      }

      add(JFXPanel().apply { 
        val pane = Pane(timelapseSlider)
        scene = Scene(pane)
        timelapseSlider.prefWidthProperty().bind(pane.widthProperty())
      }, constraints)

      add(JFXPanel().apply { scene = Scene(commitInformationPane) }, constraints)

      border = BorderFactory.createEmptyBorder(NO_PADDING.toInt(), NO_PADDING.toInt(), PADDING.toInt(), NO_PADDING.toInt())
    }

    JPanel(BorderLayout()).apply {
      add(insertionsAreaChart, CENTER)
      add(sliderAndInformationPanel, SOUTH)
    }
  }

  private val centerPanel = JPanel(BorderLayout()).apply {
    add(readingPane, CENTER)
    add(sliderPanel, NORTH)
  }

  private fun getTitle(changedFile: ChangedFile): String {
    return when (changedFile) {
      is Addition -> "[New File] ${changedFile.filePath}"
      is Modification -> "[Modified] ${changedFile.filePath}"
      is Deletion -> "[Deleted] ${changedFile.filePath}"
      is Rename -> "[Renamed] ${changedFile.oldFilePath} => ${changedFile.filePath}"
    }
  }

  private val changedFilesPane by lazy(NONE) { ChangedFilesPane(gitRepository, this) }

  private val peoplePane by lazy(NONE) { PeoplePane(gitRepository) }

  private val fileExplorerPane = FileExplorerPane(project, gitRepository, this)

  private val rootPanel = JPanel().apply {
    layout = BorderLayout()
    add(centerPanel, CENTER)
    add(fileExplorerPane.apply {
      preferredSize = Dimension(FILE_EXPLORER_WIDTH, MATCH_PARENT)
    }, WEST)

    val rightPanel = JFXPanel().apply {
      scene = Scene(GridPane().apply {
        add(changedFilesPane, 0, 0)
        add(peoplePane, 0, 1)

        columnConstraints.add(ColumnConstraints(CHANGES_WIDTH.toDouble()))
        with(rowConstraints) {
          add(RowConstraints().apply { percentHeight = 50.0 }) // Row 1 (Changed Files)
          add(RowConstraints().apply { percentHeight = 50.0 }) // Row 2 (People)
        }
      })
    }
    add(rightPanel.apply { preferredSize = Dimension(CHANGES_WIDTH, MATCH_PARENT) }, EAST)
  }

  private val timelapseFrame = JFrame(APP_NAME).apply {
    minimumSize = Dimension(WIDTH, HEIGHT)
    defaultCloseOperation = EXIT_ON_CLOSE
    extendedState = MAXIMIZED_BOTH
    setLocationRelativeTo(null)
    contentPane.add(rootPanel)

    // Key listener
    KeyboardFocusManager
      .getCurrentKeyboardFocusManager()
      .addKeyEventDispatcher { event ->
        val action: (() -> Unit)? = when {
          event.keyCode == VK_ESCAPE -> { { readingPane.dismissOverlap(); timelapseSlider.requestFocus() } }
          event.isAltDown && event.keyCode == VK_1 -> { { fileExplorerPane.focus() } }
          event.isAltDown && event.keyCode == VK_2 -> this@TimelapseApp::moveFocusToReadingPane
          event.isAltDown && event.keyCode == VK_3 -> { { changedFilesPane.focusOnList() } }
          else -> null
        }
        action?.invoke()
        action != null
      }
  }

  override fun run() {
    timelapseFrame.isVisible = true
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
        .map { it.insertions }
        .map(::Commit)
    }

    // Pair slider with change history
    with(timelapseSlider) {
      max = changes.lastIndex.toDouble()
      value = 0.0
      debug { "Setting slider's maximum to $max, value to $value" }
    }

    // Show code now
    val (previousChange, selectedChange) = getChanges(changes, 0)
    showCode(filePath, previousChange, selectedChange)
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
    previousChange: Change?,
    selectedChange: Change
  ) {
    val maybeNoPreviousRevisions = previousChange == null
    val maybeParentCommit = gitRepository.resolve("${selectedChange.commitId}^")
    val isInitialCommit = maybeNoPreviousRevisions &&
        maybeParentCommit?.name?.startsWith(selectedChange.commitId) == true

    val diffText = if (isInitialCommit) {
      gitRepository.getChangeText(filePath, selectedChange.commitId)
    } else {
      gitRepository.getDiff(selectedChange.commitId, filePath)
    }

    val diffSpans = if (isInitialCommit) {
      listOf(Insertion(diffText))
    } else {
      FormattedDiff.from(diffText).lines
    }
    readingPane.showMainDiff(filePath, diffSpans)

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
    val spans = FormattedDiff.from(gitRepository.getDiff(commitId, changedFile.filePath)).lines
    readingPane.showOverlappingDiff(getTitle(changedFile), spans)
  }
}

internal fun debug(message: () -> String) {
  println(message())
}
