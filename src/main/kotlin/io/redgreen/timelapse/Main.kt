package io.redgreen.timelapse

import io.redgreen.timelapse.domain.Change
import io.redgreen.timelapse.domain.Commit
import io.redgreen.timelapse.domain.getCommit
import io.redgreen.timelapse.domain.getCommitHistoryText
import io.redgreen.timelapse.domain.getDiff
import io.redgreen.timelapse.domain.getFilePaths
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
import java.awt.BorderLayout.WEST
import java.awt.Color.BLACK
import java.awt.Dimension
import java.awt.Font
import java.awt.Font.PLAIN
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.BoxLayout.Y_AXIS
import javax.swing.JFrame
import javax.swing.JFrame.EXIT_ON_CLOSE
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JSlider
import javax.swing.JTextPane
import javax.swing.JTree
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants
import javax.swing.tree.DefaultMutableTreeNode

private const val SPACING = 10
private const val APP_NAME = "Timelapse"
private const val COMMIT_INFORMATION_SEPARATOR = " • "
private const val GIT_PATH_SEPARATOR = '/'

private typealias DirectoryPath = String

class TimelapseCommand : Runnable {
  @Option(names = ["--debug"])
  private var isDebug: Boolean = false

  @Option(names = ["--project"])
  private var project: String = "."

  @Option(names = ["--file"])
  private var filePath: String = ""

  override fun run() {
    debug = isDebug
    buildAndShowGui()
  }

  private fun buildAndShowGui() {
    val width = 1024
    val height = 768

    // Open Git repository
    val gitRepository = openGitRepository(File(project))

    val insertionsAreaChart = AreaChart().apply { preferredSize = Dimension(width, 100) }
    val codeTextPane = JTextPane().apply {
      font = Font("monospaced", PLAIN, 15)
      isEditable = false
    }
    val timelapseSlider = JSlider()

    val commitInformationLabel = JLabel()

    // Slider
    val sliderPanel = JPanel().apply {
      layout = BoxLayout(this, Y_AXIS)
      add(Box.createRigidArea(Dimension(width, SPACING)))
      add(commitInformationLabel)
      add(timelapseSlider)
      add(Box.createRigidArea(Dimension(width, SPACING)))
    }

    // Get all file paths inside the repository
    val filePaths = gitRepository.getFilePaths()

    // File explorer
    val rootTreeNode = DefaultMutableTreeNode().apply {
      userObject = project.substring(project.lastIndexOf(File.separatorChar) + 1, project.length)

      buildFileExplorerTree(this, filePaths)
    }

    val fileExplorerTree = JTree(rootTreeNode).apply {
      preferredSize = Dimension(320, 0)
    }

    fileExplorerTree.addMouseListener(object : MouseAdapter() {
      override fun mouseClicked(e: MouseEvent) {
        val selectedPath = fileExplorerTree.getPathForLocation(e.x, e.y)
        println("${selectedPath?.parentPath} -> ${selectedPath?.lastPathComponent}")
      }
    })

    val rootPanel = JPanel()
    with(rootPanel) {
      layout = BorderLayout()
      add(insertionsAreaChart, PAGE_START)
      add(sliderPanel, PAGE_END)
      add(JScrollPane(codeTextPane), CENTER)
      add(fileExplorerTree, WEST)
    }

    // Get change history
    val changesInAscendingOrder = parseGitFollowOutput(getCommitHistoryText(project, filePath))
      .reversed()

    // Pair area chart with insertions
    with(insertionsAreaChart) {
      commits = changesInAscendingOrder.map { it.insertions }.map(::Commit)
    }

    // Pair slider with change history 
    with(timelapseSlider) {
      maximum = changesInAscendingOrder.lastIndex
      addChangeListener {
        val changeIndex = timelapseSlider.value
        val (previousChange, selectedChange) = getChanges(changesInAscendingOrder, changeIndex)
        showCode(codeTextPane, commitInformationLabel, gitRepository, previousChange, selectedChange)
      }
    }

    // Show the latest change
    val (previousChange, selectedChange) = getChanges(changesInAscendingOrder, changesInAscendingOrder.lastIndex)
    showCode(codeTextPane, commitInformationLabel, gitRepository, previousChange, selectedChange)

    // Show JFrame
    JFrame(APP_NAME).apply {
      defaultCloseOperation = EXIT_ON_CLOSE
      setSize(width, height)
      setLocationRelativeTo(null)
      contentPane.add(rootPanel)
      isVisible = true
    }
  }

  private fun buildFileExplorerTree(
    rootNode: DefaultMutableTreeNode,
    filePaths: List<String>
  ) {
    // Add root files
    filePaths
      .filter { !it.contains(GIT_PATH_SEPARATOR) }
      .onEach { rootNode.add(DefaultMutableTreeNode(it)) }

    // Add directories
    val directoryNodesRegistry = mutableMapOf<DirectoryPath, DefaultMutableTreeNode>()

    getDirectoryPaths(filePaths).onEach { directoryPath ->
      val isFirstLevel = !directoryPath.contains(GIT_PATH_SEPARATOR)
      if (isFirstLevel && directoryPath !in directoryNodesRegistry) {
        val directoryNode = DefaultMutableTreeNode(directoryPath)
        directoryNodesRegistry[directoryPath] = directoryNode

        rootNode.add(directoryNode)
      } else if (directoryPath !in directoryNodesRegistry) {
        val parentPath = directoryPath.substring(0, directoryPath.lastIndexOf(GIT_PATH_SEPARATOR))
        val childDirectory = directoryPath.substring(directoryPath.lastIndexOf(GIT_PATH_SEPARATOR) + 1)
        val childDirectoryNode = DefaultMutableTreeNode(childDirectory)
        directoryNodesRegistry[directoryPath] = childDirectoryNode

        val parentDirectoryNode = directoryNodesRegistry[parentPath]!!
        parentDirectoryNode.add(childDirectoryNode)
      }
    }

    // Add files
    filePaths
      .onEach { filePath ->
        if (filePath.contains(GIT_PATH_SEPARATOR)) {
          val parentPath = filePath.substring(0, filePath.lastIndexOf(GIT_PATH_SEPARATOR))
          val fileName = filePath.substring(filePath.lastIndexOf(GIT_PATH_SEPARATOR) + 1)
          directoryNodesRegistry[parentPath]!!.add(DefaultMutableTreeNode(fileName))
        }
      }
  }

  private fun getDirectoryPaths(filePaths: List<String>): List<String> {
    val directoryPaths = filePaths
      .filter { it.contains(GIT_PATH_SEPARATOR) }
      .map { it.substring(0, it.lastIndexOf(GIT_PATH_SEPARATOR)) }
      .distinct()
      .sorted()

    val rootDirectories = directoryPaths
      .filter { !it.contains(GIT_PATH_SEPARATOR) }

    val nonRootDirectories = directoryPaths
      .filter { it.contains(GIT_PATH_SEPARATOR) }
      .flatMap { directoryPath ->
        directoryPath.split(GIT_PATH_SEPARATOR)
          .drop(1)
          .scan(directoryPath.split(GIT_PATH_SEPARATOR).first()) { traversedPath, currentDirectory ->
            "$traversedPath$GIT_PATH_SEPARATOR$currentDirectory"
          }
      }

    return (rootDirectories + nonRootDirectories)
      .distinct()
      .sorted()
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
    commitInformationLabel: JLabel,
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
    commitInformationLabel.text = getCommitInformation(gitRepository, selectedChange)
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

  private fun getCommitInformation(
    gitRepository: Repository,
    selectedChange: Change
  ): String {
    val commit = gitRepository.getCommit(selectedChange.commitId)

    return StringBuilder()
      .append(selectedChange.commitId)
      .append(COMMIT_INFORMATION_SEPARATOR)
      .append(selectedChange.message)
      .append(COMMIT_INFORMATION_SEPARATOR)
      .append("${commit.authorIdent.name} <${commit.authorIdent.emailAddress}>")
      .toString()
  }
}

fun main(args: Array<String>) {
  CommandLine(TimelapseCommand()).execute(*args)
}
