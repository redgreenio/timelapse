package io.redgreen.timelapse.fileexplorer.view

import io.redgreen.timelapse.datastructures.NodeTransformer
import io.redgreen.timelapse.datastructures.Tree
import io.redgreen.timelapse.datastructures.Tree.Node
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
import javafx.scene.control.Label
import javafx.scene.control.SelectionMode.SINGLE
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import org.eclipse.jgit.lib.Constants.HEAD
import org.eclipse.jgit.lib.Repository
import java.io.File
import java.time.LocalDate
import kotlin.LazyThreadSafetyMode.NONE
import kotlin.system.measureTimeMillis

private const val GIT_PATH_SEPARATOR = '/'

class FileExplorerPane(
  private val projectPath: String,
  private val gitRepository: Repository,
  private val fileSelectionListener: FileSelectionListener
) : JFXPanel() {
  interface FileSelectionListener {
    fun onFilePathSelected(filePath: String, startDateEndDate: Pair<LocalDate, LocalDate>? = null)
  }

  private var startDateEndDate: Pair<LocalDate, LocalDate>? = null

  private val fileExplorerTreeView by lazy(NONE) {
    val treeView = TreeView<String>().apply { selectionModel.selectionMode = SINGLE }

    treeView.selectionModel.selectedItemProperty().addListener { _, _, selectedItem ->
      val pathBuilder = StringBuilder()
      var node: TreeItem<String>? = selectedItem
      while (node != null) {
        with(pathBuilder) {
          if (node?.parent != null) {
            insert(0, node?.value)
            insert(0, GIT_PATH_SEPARATOR)
          }
        }
        node = node?.parent
      }

      val rootSelected = pathBuilder.isEmpty()
      if (rootSelected) {
        return@addListener
      }

      val fullFilePath = pathBuilder.substring(1).toString()
      debug { "Selected path: $fullFilePath" }

      if (selectedItem.isLeaf) {
        fileSelectionListener.onFilePathSelected(fullFilePath, startDateEndDate)
      }
    }

    treeView
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

  private val fileCountLabel by lazy(NONE) { Label() }

  init {
    timeSpanComboBox = ComboBox(timeSpans)
    scene = Scene(BorderPane().apply {
      val vBox = VBox(Label(" • File Explorer • "), timeSpanComboBox)
      top = vBox
      timeSpanComboBox.prefWidthProperty().bind(widthProperty())

      bottom = fileCountLabel
      fileCountLabel.prefWidthProperty().bind(widthProperty())

      center = fileExplorerTreeView
      fileExplorerTreeView.prefWidthProperty().bind(widthProperty())
    })

    timeSpanComboBox.valueProperty().addListener { _, _, newValue ->
      val loadingTimeMillis = measureTimeMillis {
        val projectName = getProjectName()
        when(newValue) {
          AllFiles -> {
            val projectFiles = gitRepository.getFilePaths().map { "$projectName/$it" }
            startDateEndDate = null
            buildFileExplorerTree(projectName, projectFiles)
          }

          is TMinusDays -> {
            val head = gitRepository.resolve(HEAD).let { gitRepository.parseCommit(it) }

            val endDate = head.committerIdent.`when`.time.toLocalDateTime().toLocalDate()
            val startDate = endDate.minusDays(newValue.days.toLong())

            startDateEndDate = startDate to endDate

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
    fileCountLabel.text = "${filePaths.size} file(s)"

    val filePathsTree = Tree.create(projectName) { filePath -> filePath.split(GIT_PATH_SEPARATOR) }
    filePaths.forEach(filePathsTree::insert)

    val treeItemNodeTransformer = object : NodeTransformer<Node<String>, TreeItem<String>> {
      override fun create(node: Node<String>): TreeItem<String> {
        return TreeItem(node.value).apply {
          node.children.map(::create).onEach { children.add(it) }
        }
      }
    }
    fileExplorerTreeView.root = filePathsTree.transform(treeItemNodeTransformer)
  }

  fun focus() {
    Platform.runLater { fileExplorerTreeView.requestFocus() }
  }

  private fun getProjectName(): String =
    projectPath.split(File.separator).last()
}
