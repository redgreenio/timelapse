package io.redgreen.timelapse

import humanize.Humanize.naturalTime
import io.redgreen.timelapse.changedfiles.contracts.ReadingAreaContract
import io.redgreen.timelapse.changedfiles.view.ChangedFilesPane
import io.redgreen.timelapse.datastructures.NodeTransformer
import io.redgreen.timelapse.datastructures.Tree
import io.redgreen.timelapse.datastructures.Tree.Node
import io.redgreen.timelapse.domain.Change
import io.redgreen.timelapse.domain.Commit
import io.redgreen.timelapse.domain.getCommit
import io.redgreen.timelapse.domain.getCommitHistoryText
import io.redgreen.timelapse.domain.getDiff
import io.redgreen.timelapse.domain.getFilePaths
import io.redgreen.timelapse.domain.openGitRepository
import io.redgreen.timelapse.domain.parseGitFollowOutput
import io.redgreen.timelapse.domain.readFileFromCommitId
import io.redgreen.timelapse.people.view.PeoplePane
import io.redgreen.timelapse.ui.ACTION_MAP_KEY_NO_OP
import io.redgreen.timelapse.ui.KEY_STROKE_DOWN
import io.redgreen.timelapse.ui.KEY_STROKE_UP
import io.redgreen.timelapse.ui.ReadingPane
import io.redgreen.timelapse.vcs.ChangedFile
import io.redgreen.timelapse.vcs.ChangedFile.Addition
import io.redgreen.timelapse.vcs.ChangedFile.Deletion
import io.redgreen.timelapse.vcs.ChangedFile.Modification
import io.redgreen.timelapse.vcs.ChangedFile.Rename
import io.redgreen.timelapse.visuals.AreaChart
import io.redgreen.timelapse.visuals.DiffSpan.Insertion
import io.redgreen.timelapse.visuals.FormattedDiff
import io.redgreen.timelapse.visuals.formatDate
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
import java.awt.GridLayout
import java.awt.KeyboardFocusManager
import java.awt.event.KeyEvent.VK_1
import java.awt.event.KeyEvent.VK_2
import java.awt.event.KeyEvent.VK_3
import java.awt.event.KeyEvent.VK_ESCAPE
import java.io.File
import javax.swing.BorderFactory
import javax.swing.JComponent.WHEN_FOCUSED
import javax.swing.JFrame
import javax.swing.JFrame.EXIT_ON_CLOSE
import javax.swing.JFrame.MAXIMIZED_BOTH
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JSlider
import javax.swing.JTree
import javax.swing.KeyStroke
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION
import kotlin.LazyThreadSafetyMode.NONE
import kotlin.math.ceil
import kotlin.system.measureTimeMillis

private const val APP_NAME = "Timelapse"
private const val COMMIT_INFORMATION_SEPARATOR = " • "
private const val GIT_PATH_SEPARATOR = '/'

private const val WIDTH = 1024
private const val HEIGHT = 768
private const val AREA_CHART_HEIGHT = 100
private const val FILE_EXPLORER_WIDTH = 320
private const val CHANGES_WIDTH = 400
private const val MATCH_PARENT = 0

private const val NO_PADDING = 0
private const val PADDING = 10

class TimelapseApp(private val project: String) : Runnable, ReadingAreaContract {
  private val gitRepository by lazy { openGitRepository(File(project)) }
  private lateinit var changesInAscendingOrder: List<Change>
  private lateinit var filePath: String

  private val timelapseSlider = JSlider().apply {
    maximum = 0
    border = BorderFactory.createEmptyBorder(PADDING, NO_PADDING, NO_PADDING, NO_PADDING)

    addChangeListener {
      if (!::changesInAscendingOrder.isInitialized) {
        return@addChangeListener
      }

      val changeIndex = this.value
      val (previousChange, selectedChange) = getChanges(changesInAscendingOrder, changeIndex)

      // Show code on slider move
      showCode(filePath, previousChange, selectedChange)

      // Update anchor in area chart
      insertionsAreaChart.setAnchorIndex(changeIndex)
    }

    with(getInputMap(WHEN_FOCUSED)) {
      put(KeyStroke.getKeyStroke(KEY_STROKE_UP), ACTION_MAP_KEY_NO_OP)
      put(KeyStroke.getKeyStroke(KEY_STROKE_DOWN), ACTION_MAP_KEY_NO_OP)
    }
  }

  private val commitInformationLabel = JLabel().apply {
    border = BorderFactory.createEmptyBorder(PADDING, PADDING, NO_PADDING, PADDING)
  }

  private val insertionsAreaChart by lazy(NONE) {
    AreaChart().apply { preferredSize = Dimension(WIDTH, AREA_CHART_HEIGHT) }
  }

  private val readingPane = ReadingPane()

  private val sliderPanel by lazy(NONE) {
    val sliderAndInformationPanel = JPanel(GridBagLayout()).apply {
      val constraints = GridBagConstraints().apply {
        weightx = 1.0
        fill = HORIZONTAL
        gridwidth = REMAINDER
      }
      add(timelapseSlider, constraints)
      add(commitInformationLabel, constraints)
      border = BorderFactory.createEmptyBorder(NO_PADDING, NO_PADDING, PADDING, NO_PADDING)
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

  private val fileExplorerTree = JTree().apply {
    selectionModel.selectionMode = SINGLE_TREE_SELECTION

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
          selectFile(fullFilePath)
        }
      }
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

  private val changedFilesPane = ChangedFilesPane(gitRepository, this)

  private val peoplePane = PeoplePane(gitRepository)

  private val rootPanel = JPanel().apply {
    layout = BorderLayout()
    add(centerPanel, CENTER)
    add(JScrollPane(fileExplorerTree).apply {
      preferredSize = Dimension(FILE_EXPLORER_WIDTH, MATCH_PARENT)
      border = BorderFactory.createTitledBorder("File Explorer")
    }, WEST)

    val rightPanel = JPanel().apply {
      layout = GridLayout(2, 1)
    }
    with(rightPanel) {
      add(changedFilesPane)
      add(peoplePane)
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
          event.isAltDown && event.keyCode == VK_1 -> { { fileExplorerTree.requestFocus() } }
          event.isAltDown && event.keyCode == VK_2 -> this@TimelapseApp::moveFocusToReadingPane
          event.isAltDown && event.keyCode == VK_3 -> { { changedFilesPane.focusOnList() } }
          else -> null
        }
        action?.invoke()
        action != null
      }
  }

  override fun run() {
    val loadingTimeMillis = measureTimeMillis {
      val projectName = getProjectName()
      val projectFilePaths = gitRepository.getFilePaths().map { "$projectName$GIT_PATH_SEPARATOR$it" }
      buildFileExplorerTree(projectName, projectFilePaths)
    }

    debug { "Building file explorer took ${loadingTimeMillis}ms." }

    // Show JFrame
    timelapseFrame.isVisible = true
  }

  private fun getProjectName(): String =
    project.split(File.separator).last()

  private fun moveFocusToReadingPane() {
    if (readingPane.isShowingOverlap()) {
      readingPane.focusOnOverlap()
    } else {
      timelapseSlider.requestFocus()
    }
  }

  private fun buildFileExplorerTree(projectName: String, filePaths: List<String>) {
    debug { "Found ${filePaths.size} files." }

    val filePathsTree = Tree.create(projectName) { filePath -> filePath.split(GIT_PATH_SEPARATOR) }
    filePaths.forEach(filePathsTree::insert)

    val defaultMutableTreeNodeTransformer = object : NodeTransformer<Node<String>, DefaultMutableTreeNode> {
      override fun create(node: Node<String>): DefaultMutableTreeNode {
        return DefaultMutableTreeNode(node.value, node.children.isNotEmpty()).apply {
          node.children.map(::create).onEach(this::add)
        }
      }
    }
    val rootTreeNode = filePathsTree.transform(defaultMutableTreeNodeTransformer)
    fileExplorerTree.model = DefaultTreeModel(rootTreeNode)
  }

  private fun selectFile(filePath: String) {
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
    }

    // Pair slider with change history
    with(timelapseSlider) {
      maximum = changesInAscendingOrder.lastIndex
      value = 0
      debug { "Setting slider's maximum to $maximum, value to $value" }
    }

    // Show code now
    val (previousChange, selectedChange) = getChanges(changesInAscendingOrder, 0)
    showCode(filePath, previousChange, selectedChange)
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
    filePath: String,
    previousChange: Change?,
    selectedChange: Change
  ) {
    val noPreviousRevisions = previousChange == null
    val diffText = if (noPreviousRevisions) {
      gitRepository.getChangeText(filePath, selectedChange.commitId)
    } else {
      gitRepository.getDiff(selectedChange.commitId, filePath)
    }

    val diffSpans = if (noPreviousRevisions) {
      listOf(Insertion(diffText))
    } else {
      FormattedDiff.from(diffText).spans
    }
    readingPane.showMainDiff(filePath, diffSpans)

    commitInformationLabel.text = getCommitInformation(gitRepository, selectedChange)

    changedFilesPane.selectFileAndRevision(filePath, selectedChange.commitId)
    peoplePane.selectFileAndRevision(filePath, selectedChange.commitId)
  }

  private fun Repository.getChangeText(
    filePath: String,
    commitId: String
  ): String {
    return readFileFromCommitId(commitId, filePath)
  }

  private fun getCommitInformation(
    gitRepository: Repository,
    selectedChange: Change
  ): String {
    val commit = gitRepository.getCommit(selectedChange.commitId)
    val position = "${timelapseSlider.value + 1}/${changesInAscendingOrder.size}"
    val progressPercent = ((timelapseSlider.value + 1).toDouble() / changesInAscendingOrder.size) * 100
    val progressPercentText = String.format("%.2f", ceil(progressPercent)).replace(".00", "")
    val committedDate = commit.committerIdent.`when`
    val authorAndCommitDate = formatDate(commit.authorIdent.`when`, committedDate)
    val committedNaturalTime = naturalTime(committedDate)

    return """
      <html>
        ${selectedChange.message}<br />
        $authorAndCommitDate ($committedNaturalTime)<br /><br />
        Commit $position $COMMIT_INFORMATION_SEPARATOR $progressPercentText%<br />
        <code>${commit.name}</code> $COMMIT_INFORMATION_SEPARATOR ${commit.authorIdent.name}  &lt;${commit.authorIdent.emailAddress}&gt;
      </html>
    """.trimIndent()
  }

  override fun showChangedFileDiff(commitId: String, changedFile: ChangedFile) {
    val spans = FormattedDiff.from(gitRepository.getDiff(commitId, changedFile.filePath)).spans
    readingPane.showOverlappingDiff(getTitle(changedFile), spans)
  }
}

internal fun debug(message: () -> String) {
  println(message())
}
