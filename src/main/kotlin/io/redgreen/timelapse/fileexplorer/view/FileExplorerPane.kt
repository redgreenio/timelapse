package io.redgreen.timelapse.fileexplorer.view

import io.redgreen.timelapse.datastructures.NodeTransformer
import io.redgreen.timelapse.datastructures.Tree
import io.redgreen.timelapse.debug
import java.awt.GridLayout
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeSelectionModel

private const val GIT_PATH_SEPARATOR = '/'

class FileExplorerPane(private val fileSelectionListener: FileSelectionListener) : JPanel(GridLayout(1, 1)) {
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

  init {
    add(JScrollPane(fileExplorerTree))
  }

  fun buildFileExplorerTree(projectName: String, filePaths: List<String>) {
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
}
