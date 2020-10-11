package io.redgreen.timelapse

import io.redgreen.timelapse.domain.Change
import io.redgreen.timelapse.domain.Commit
import io.redgreen.timelapse.domain.getCommitHistoryText
import io.redgreen.timelapse.domain.getDiff
import io.redgreen.timelapse.domain.openGitRepository
import io.redgreen.timelapse.domain.parseGitFollowOutput
import io.redgreen.timelapse.domain.readFileFromCommitId
import io.redgreen.timelapse.visuals.AreaChart
import io.redgreen.timelapse.visuals.DiffSpan.Insertion
import io.redgreen.timelapse.visuals.FormattedDiff
import io.redgreen.timelapse.visuals.debug.debug
import org.eclipse.jgit.lib.Repository
import picocli.CommandLine
import picocli.CommandLine.Option
import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.PAGE_END
import java.awt.BorderLayout.PAGE_START
import java.awt.Color.BLACK
import java.awt.Dimension
import java.awt.Font
import java.awt.Font.PLAIN
import java.io.File
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.BoxLayout.Y_AXIS
import javax.swing.JFrame
import javax.swing.JFrame.EXIT_ON_CLOSE
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JSlider
import javax.swing.JTextPane
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants

private const val SPACING = 10
private const val APP_NAME = "Timelapse"

class TimelapseCommand : Runnable {
  @Option(names = ["--debug"])
  private var isDebug: Boolean = false

  @Option(names = ["--project"])
  private var project: String = "."

  @Option(names = ["--file"])
  private var filePath: String = ""

  override fun run() {
    debug = isDebug
    println("$filePath in $project")
    buildAndShowGui()
  }

  private fun buildAndShowGui() {
    val width = 1024
    val height = 768

    val insertionsAreaChart = AreaChart().apply { preferredSize = Dimension(width, 100) }
    val codeTextPane = JTextPane().apply {
      font = Font("monospaced", PLAIN, 15)
      isEditable = false
    }
    val timelapseSlider = JSlider()

    // Slider
    val sliderPanel = JPanel().apply {
      layout = BoxLayout(this, Y_AXIS)
      add(Box.createRigidArea(Dimension(width, SPACING)))
      add(timelapseSlider)
      add(Box.createRigidArea(Dimension(width, SPACING)))
    }

    val rootPanel = JPanel()
    with(rootPanel) {
      layout = BorderLayout()
      add(insertionsAreaChart, PAGE_START)
      add(sliderPanel, PAGE_END)
      add(JScrollPane(codeTextPane), CENTER)
    }

    JFrame(APP_NAME).apply {
      defaultCloseOperation = EXIT_ON_CLOSE
      setSize(width, height)
      setLocationRelativeTo(null)
      contentPane.add(rootPanel)
      isVisible = true
    }

    // Get change history
    val changesInAscendingOrder = parseGitFollowOutput(getCommitHistoryText(project, filePath))
      .reversed()

    // Pair area chart with insertions
    with(insertionsAreaChart) {
      commits = changesInAscendingOrder.map { it.insertions }.map(::Commit)
    }

    // Open Git repository
    val gitRepository = openGitRepository(File(project))

    // Pair slider with change history 
    with(timelapseSlider) {
      maximum = changesInAscendingOrder.lastIndex
      addChangeListener {
        val changeIndex = timelapseSlider.value
        val (previousChange, selectedChange) = getChanges(changesInAscendingOrder, changeIndex)
        showCode(codeTextPane, gitRepository, previousChange, selectedChange)
      }
    }

    // Show the latest change
    val (previousChange, selectedChange) = getChanges(changesInAscendingOrder, changesInAscendingOrder.lastIndex)
    showCode(codeTextPane, gitRepository, previousChange, selectedChange)
  }

  private fun getChanges(
    changesInAscendingOrder: List<Change>,
    changeIndex: Int
  ): Pair<Change?, Change> {
    val previousChange = if (changeIndex == 0) null else changesInAscendingOrder[changeIndex - 1]
    val selectedChange = changesInAscendingOrder[changeIndex]
    return Pair(previousChange, selectedChange)
  }

  private fun showCode(
    codeTextPane: JTextPane,
    gitRepository: Repository,
    previousChange: Change?,
    selectedChange: Change
  ) {
    val noPreviousRevisions = previousChange == null
    val diffText = if (noPreviousRevisions) {
      getChangeText(gitRepository, filePath, selectedChange)
    } else {
      gitRepository.getDiff(filePath, previousChange!!.commitId, selectedChange.commitId)
    }

    codeTextPane.showDiff(diffText, noPreviousRevisions)
  }

  private fun JTextPane.showDiff(diffText: String, noPreviousRevisions: Boolean) {
    // Clear existing text
    this.styledDocument.remove(0, this.document.length)

    // Add new text
    val attributeSet = SimpleAttributeSet()
    val style = this.addStyle(null, null)
    this.setCharacterAttributes(attributeSet, true)
    StyleConstants.setForeground(style, BLACK)

    val spans = if (noPreviousRevisions) {
      listOf(Insertion(diffText))
    } else {
      FormattedDiff.from(diffText).spans
    }

    spans
      .onEach { span ->
        StyleConstants.setBackground(style, span.backgroundColor())
        styledDocument.insertString(styledDocument.length, span.text(), style)
      }
  }

  private fun getChangeText(
    repository: Repository,
    filePath: String,
    currentChange: Change
  ): String {
    return repository.readFileFromCommitId(currentChange.commitId, filePath)
  }
}

fun main(args: Array<String>) {
  CommandLine(TimelapseCommand()).execute(*args)
}
