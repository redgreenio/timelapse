package io.redgreen.timelapse.fileexplorer.view

import io.redgreen.timelapse.datastructures.NodeTransformer
import io.redgreen.timelapse.datastructures.Tree
import io.redgreen.timelapse.debug
import io.redgreen.timelapse.domain.getFilePaths
import io.redgreen.timelapse.fileexplorer.view.FileExplorerSelection.AllFiles
import io.redgreen.timelapse.fileexplorer.view.FileExplorerSelection.TMinusDays
import io.redgreen.timelapse.foo.toLocalDateTime
import io.redgreen.timelapse.vcs.git.GitRepositoryService
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.control.ComboBox
import javafx.scene.layout.HBox
import org.eclipse.jgit.lib.Constants.HEAD
import org.eclipse.jgit.lib.Repository
import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.NORTH
import java.io.File
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeSelectionModel
import kotlin.LazyThreadSafetyMode.NONE
import kotlin.system.measureTimeMillis

private const val GIT_PATH_SEPARATOR = '/'

class FileExplorerPane(
  private val projectPath: String,
  private val gitRepository: Repository,
  private val fileSelectionListener: FileSelectionListener
) : JPanel(BorderLayout()) {
  interface FileSelectionListener {
    fun onFilePathSelected(filePath: String)
  }

  private val fileExplorerTree = JTree().apply {
    selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION

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
          fileSelectionListener.onFilePathSelected(fullFilePath)
        }
      }
    }
  }

  private val timeSpanComboBox: ComboBox<FileExplorerSelection>
  private val timeSpans = FXCollections.observableArrayList(
    AllFiles,
    TMinusDays(7),
    TMinusDays(30),
    TMinusDays(60),
    TMinusDays(90),
  )

  private val gitRepositoryService by lazy(NONE) { GitRepositoryService(gitRepository) }

  init {
    add(JFXPanel().apply {
      timeSpanComboBox = ComboBox(timeSpans)
      val hBox = HBox()
      timeSpanComboBox.prefWidthProperty().bind(hBox.widthProperty())
      scene = Scene(hBox.apply { children.add(timeSpanComboBox) })
    }, NORTH)

    add(JScrollPane(fileExplorerTree), CENTER)

    timeSpanComboBox.valueProperty().addListener { _, _, newValue ->
      val loadingTimeMillis = measureTimeMillis {
        val projectName = getProjectName()
        when(newValue) {
          AllFiles -> {
            val projectFiles = gitRepository.getFilePaths().map { "$projectName/$it" }
            buildFileExplorerTree(projectName, projectFiles)
          }

          is TMinusDays -> {
            val head = gitRepository.resolve(HEAD).let { gitRepository.parseCommit(it) }

            val endDate = head.committerIdent.`when`.time.toLocalDateTime().toLocalDate()
            val startDate = endDate.minusDays(newValue.days.toLong())

            debug { "Attempting to get commits between $startDate and $endDate" }

            gitRepositoryService
              .getFirstCommitOnOrAfter(startDate)
              .flatMap { gitRepositoryService.getChangedFilePaths(head.name, it) }
              .subscribe(
                { projectFiles -> buildFileExplorerTree(projectName, projectFiles.map { "$projectName/$it" }) },
                { it.printStackTrace() }
              )
          }
        }
      }

      debug { "Selected $newValue from the file explorer combo box." }
      debug { "Building file explorer took ${loadingTimeMillis}ms." }
    }

    Platform.runLater { timeSpanComboBox.selectionModel.select(0) }
  }

  private fun buildFileExplorerTree(projectName: String, filePaths: List<String>) {
    debug { "Found ${filePaths.size} files." }

    val filePathsTree = Tree.create(projectName) { filePath -> filePath.split(GIT_PATH_SEPARATOR) }
    filePaths.forEach(filePathsTree::insert)

    val defaultMutableTreeNodeTransformer = object : NodeTransformer<Tree.Node<String>, DefaultMutableTreeNode> {
      override fun create(node: Tree.Node<String>): DefaultMutableTreeNode {
        return DefaultMutableTreeNode(node.value, node.children.isNotEmpty()).apply {
          node.children.map(::create).onEach(this::add)
        }
      }
    }
    val rootTreeNode = filePathsTree.transform(defaultMutableTreeNodeTransformer)
    fileExplorerTree.model = DefaultTreeModel(rootTreeNode)
  }

  fun focus() {
    fileExplorerTree.requestFocus()
  }

  private fun getProjectName(): String =
    projectPath.split(File.separator).last()
}
