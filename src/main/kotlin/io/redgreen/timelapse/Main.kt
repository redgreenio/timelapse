package io.redgreen.timelapse

import io.redgreen.timelapse.domain.Change
import io.redgreen.timelapse.domain.Commit
import io.redgreen.timelapse.domain.getCommitHistoryText
import io.redgreen.timelapse.domain.openGitRepository
import io.redgreen.timelapse.domain.parseGitFollowOutput
import io.redgreen.timelapse.visuals.AreaChart
import io.redgreen.timelapse.visuals.debug.debug
import org.eclipse.jgit.lib.Repository
import picocli.CommandLine
import picocli.CommandLine.Option
import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.PAGE_END
import java.awt.BorderLayout.PAGE_START
import java.awt.Dimension
import java.io.File
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.BoxLayout.Y_AXIS
import javax.swing.JFrame
import javax.swing.JFrame.EXIT_ON_CLOSE
import javax.swing.JPanel
import javax.swing.JSlider
import javax.swing.JTextArea

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
    val insertions = listOf(
      3, 8, 1, 3, 1, 1, 2, 2, 3, 2, 1, 1, 11, 5, 11, 31, 6, 6, 1, 2, 14,
      1, 1, 7, 1, 1, 1, 1, 2, 2, 4, 2, 4, 2, 1, 1, 2, 1, 11, 8, 6, 12, 10,
    )

    val insertionsAreaChart = AreaChart().apply { preferredSize = Dimension(width, 100) }
    val codeTextArea = JTextArea()
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
      add(insertionsAreaChart.apply { this.commits = insertions.map(::Commit) }, PAGE_START)
      add(sliderPanel, PAGE_END)
      add(codeTextArea.apply { isEnabled = false }, CENTER)
    }

    JFrame(APP_NAME).apply {
      defaultCloseOperation = EXIT_ON_CLOSE
      setSize(width, height)
      setLocationRelativeTo(null)
      contentPane.add(rootPanel)
      isVisible = true
    }

    // Open Git repository
    val gitRepository = openGitRepository(File(project))

    // Pair slider with change history 
    val changesInAscendingOrder = parseGitFollowOutput(getCommitHistoryText(filePath))
      .reversed()
    println(changesInAscendingOrder.size)
    with(timelapseSlider) {
      maximum = changesInAscendingOrder.lastIndex
      addChangeListener {
        viewChange(gitRepository, filePath, changesInAscendingOrder[timelapseSlider.value])
      }
    }

    // Pair area chart with insertions
    with(insertionsAreaChart) {
      commits = changesInAscendingOrder.map { it.insertions }.map(::Commit)
    }
  }

  private fun viewChange(
    gitRepository: Repository,
    filePath: String,
    change: Change
  ) {
    // TODO checkout commit
    // TODO get file diff
    // TODO update in text area
    println(change.commitId)
  }
}

fun main(args: Array<String>) {
  CommandLine(TimelapseCommand()).execute(*args)
}
