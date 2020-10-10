package io.redgreen.timelapse

import io.redgreen.timelapse.domain.Change
import io.redgreen.timelapse.domain.Commit
import io.redgreen.timelapse.domain.getCommitHistoryText
import io.redgreen.timelapse.domain.openGitRepository
import io.redgreen.timelapse.domain.parseGitFollowOutput
import io.redgreen.timelapse.visuals.AreaChart
import io.redgreen.timelapse.visuals.debug.debug
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.treewalk.filter.PathFilter
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
      add(insertionsAreaChart, PAGE_START)
      add(sliderPanel, PAGE_END)
      add(codeTextArea, CENTER)
    }

    JFrame(APP_NAME).apply {
      defaultCloseOperation = EXIT_ON_CLOSE
      setSize(width, height)
      setLocationRelativeTo(null)
      contentPane.add(rootPanel)
      isVisible = true
    }

    // Get change history
    val changesInAscendingOrder = parseGitFollowOutput(getCommitHistoryText(filePath))
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
      addChangeListener { showCode(gitRepository, codeTextArea, changesInAscendingOrder[timelapseSlider.value]) }
    }

    // Show the latest change
    showCode(gitRepository, codeTextArea, changesInAscendingOrder.last())
  }

  private fun showCode(
    gitRepository: Repository,
    codeTextArea: JTextArea,
    selectedChange: Change
  ) {
    val text = getChangeText(gitRepository, filePath, selectedChange)
    codeTextArea.text = text
  }

  private fun getChangeText(
    repository: Repository,
    filePath: String,
    change: Change
  ): String {
    val commitObjectId = repository.resolve(change.commitId)
    var text: String
    RevWalk(repository).use { revWalk -> 
      val commit = revWalk.parseCommit(commitObjectId)
      val tree = commit.tree

      TreeWalk(repository).apply {
        addTree(tree)
        isRecursive = true
        filter = PathFilter.create(filePath)
      }.use { treeWalk ->
        treeWalk.next()

        val filePathObjectId = treeWalk.getObjectId(0)
        val loader = repository.open(filePathObjectId)

        text = String(loader.bytes)
      }
    }
    return text
  }
}

fun main(args: Array<String>) {
  CommandLine(TimelapseCommand()).execute(*args)
}
