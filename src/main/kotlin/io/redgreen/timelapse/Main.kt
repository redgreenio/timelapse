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
import io.redgreen.timelapse.git.getChangesInCommit
import io.redgreen.timelapse.ui.CodeTextPane
import io.redgreen.timelapse.visuals.AreaChart
import io.redgreen.timelapse.visuals.DiffSpan.Insertion
import io.redgreen.timelapse.visuals.FormattedDiff
import io.redgreen.timelapse.visuals.debug.debug
import org.eclipse.jgit.lib.Repository
import picocli.CommandLine
import picocli.CommandLine.Option
import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.EAST
import java.awt.BorderLayout.PAGE_END
import java.awt.BorderLayout.PAGE_START
import java.awt.BorderLayout.WEST
import java.awt.Dimension
import java.io.File
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.BoxLayout.Y_AXIS
import javax.swing.DefaultListModel
import javax.swing.JFrame
import javax.swing.JFrame.EXIT_ON_CLOSE
import javax.swing.JFrame.MAXIMIZED_BOTH
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JSlider
import javax.swing.JTree
import javax.swing.ListSelectionModel.SINGLE_SELECTION
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import kotlin.LazyThreadSafetyMode.NONE
import io.redgreen.timelapse.git.Change as FileChange

private const val APP_NAME = "Timelapse"
private const val COMMIT_INFORMATION_SEPARATOR = " • "
private const val GIT_PATH_SEPARATOR = '/'

private const val WIDTH = 1024
private const val HEIGHT = 768
private const val SLIDER_RIGID_AREA_SPACING = 10
private const val AREA_CHART_HEIGHT = 100
private const val FILE_EXPLORER_WIDTH = 320
private const val CHANGES_WIDTH = 400
private const val MATCH_PARENT = 0

private typealias DirectoryPath = String

class TimelapseCommand : Runnable {
  @Option(names = ["--debug"])
  private var isDebug: Boolean = false

  @Option(names = ["--project"])
  private var project: String = "."

  private lateinit var gitRepository: Repository
  private lateinit var changesInAscendingOrder: List<Change>
  private lateinit var filePath: String

  private val timelapseSlider = JSlider().apply {
    maximum = 0

    addChangeListener {
      if (!::changesInAscendingOrder.isInitialized) {
        return@addChangeListener
      }

      val changeIndex = this.value
      val (previousChange, selectedChange) = getChanges(changesInAscendingOrder, changeIndex)
      // Show code on slider move
      showCode(commitInformationLabel, gitRepository, filePath, previousChange, selectedChange)
    }
  }

  private val commitInformationLabel = JLabel()

  private val insertionsAreaChart by lazy(NONE) {
    AreaChart().apply { preferredSize = Dimension(WIDTH, AREA_CHART_HEIGHT) }
  }

  private val codeTextPane = CodeTextPane()

  private val sliderPanel by lazy(NONE) {
    JPanel().apply {
      layout = BoxLayout(this, Y_AXIS)
      add(Box.createRigidArea(Dimension(WIDTH, SLIDER_RIGID_AREA_SPACING)))
      add(commitInformationLabel)
      add(timelapseSlider)
      add(Box.createRigidArea(Dimension(WIDTH, SLIDER_RIGID_AREA_SPACING)))
    }
  }

  private val fileExplorerTree = JTree().apply {
    addTreeSelectionListener { selectionEvent ->
      selectionEvent.path?.let { selectedPath ->
        val selectedRootNode = selectedPath.parentPath == null
        if (selectedRootNode) {
          return@let
        }

        val parentPath = selectedPath.parentPath.path.drop(1).joinToString(GIT_PATH_SEPARATOR.toString())
        val fullFilePath = if (parentPath.isEmpty()) {
          selectedPath.lastPathComponent.toString()
        } else {
          "$parentPath$GIT_PATH_SEPARATOR${selectedPath.lastPathComponent}"
        }
        debug { "Selected path: $fullFilePath" }

        val isLeafNode = (selectedPath.lastPathComponent as? DefaultMutableTreeNode)?.childCount == 0
        if (isLeafNode) {
          selectFile(gitRepository, fullFilePath)
        }
      }
    }
  }

  private val changesList = JList<String>().apply {
    selectionMode = SINGLE_SELECTION
  }

  private val rootPanel = JPanel().apply {
    layout = BorderLayout()
    add(insertionsAreaChart, PAGE_START)
    add(sliderPanel, PAGE_END)
    add(codeTextPane, CENTER)
    add(JScrollPane(fileExplorerTree).apply { preferredSize = Dimension(FILE_EXPLORER_WIDTH, MATCH_PARENT) }, WEST)
    add(JScrollPane(changesList).apply { preferredSize = Dimension(CHANGES_WIDTH, MATCH_PARENT) }, EAST)
  }

  private val timelapseFrame = JFrame(APP_NAME).apply {
    minimumSize = Dimension(WIDTH, HEIGHT)
    defaultCloseOperation = EXIT_ON_CLOSE
    extendedState = MAXIMIZED_BOTH
    setLocationRelativeTo(null)
    contentPane.add(rootPanel)
  }

  override fun run() {
    debug = isDebug
    buildAndShowGui()
  }

  private fun buildAndShowGui() {
    gitRepository = openProject(project) { filePaths ->
      val rootTreeNode = DefaultMutableTreeNode().apply {
        userObject = project.substring(project.lastIndexOf(File.separatorChar) + 1, project.length)
        buildFileExplorerTree(this, filePaths)
      }
      fileExplorerTree.model = DefaultTreeModel(rootTreeNode)
    }

    // Show JFrame
    timelapseFrame.isVisible = true
  }

  private fun selectFile(gitRepository: Repository, filePath: String) {
    this.filePath = filePath

    // Get change history
    val gitFollowOutput = getCommitHistoryText(project, filePath)
    changesInAscendingOrder = parseGitFollowOutput(gitFollowOutput)
      .reversed()

    debug { "Found ${changesInAscendingOrder.size} commits for $filePath" }

    // Pair area chart with insertions
    with(insertionsAreaChart) {
      val changesMappedToCommits = changesInAscendingOrder
        .map { it.insertions }
        .map(::Commit)
      commits = changesMappedToCommits
      debug { "Updated area chart with data points: ${changesMappedToCommits.map { it.insertions }.joinToString()}" }
    }

    // Pair slider with change history
    with(timelapseSlider) {
      maximum = changesInAscendingOrder.lastIndex
      value = 0
      debug { "Setting slider's maximum to $maximum, value to $value" }
    }

    // Show code now
    val (previousChange, selectedChange) = getChanges(changesInAscendingOrder, 0)
    showCode(commitInformationLabel, gitRepository, filePath, previousChange, selectedChange)
  }

  private fun openProject(projectPath: String, onProjectOpened: (List<String>) -> Unit): Repository {
    val gitRepository = openGitRepository(File(projectPath))
    onProjectOpened(gitRepository.getFilePaths())
    return gitRepository
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
    commitInformationLabel: JLabel,
    gitRepository: Repository,
    filePath: String,
    previousChange: Change?,
    selectedChange: Change
  ) {
    val noPreviousRevisions = previousChange == null
    val diffText = if (noPreviousRevisions) {
      getChangeText(gitRepository, filePath, selectedChange)
    } else {
      gitRepository.getDiff(filePath, previousChange!!.commitId, selectedChange.commitId)
    }

    val diffSpans = if (noPreviousRevisions) {
      listOf(Insertion(diffText))
    } else {
      FormattedDiff.from(diffText).spans
    }
    codeTextPane.showDiff(diffSpans)

    commitInformationLabel.text = getCommitInformation(gitRepository, selectedChange)

    val changesInCommit = gitRepository
      .getChangesInCommit(selectedChange.commitId)
    showChanges(changesInCommit)
  }

  private fun showChanges(changes: List<FileChange>) {
    val changesListModel = DefaultListModel<String>()
    changes.onEach { changesListModel.addElement(it.filePath) }
    changesList.model = changesListModel
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

internal fun debug(message: () -> String) {
  println(message())
}
