package io.redgreen.timelapse.datastructures

class Tree<T : Comparable<T>> private constructor(
  val root: Node<T>,
  private val nodeSplitter: (T) -> List<T>
) {
  companion object {
    fun <T : Comparable<T>> create(root: T, split: (T) -> List<T>): Tree<T> {
      return Tree(Node(root), split)
    }
  }

  fun insert(value: T) {
    val nodes = nodeSplitter(value)

    var ancestor = root
    nodes.drop(1).forEachIndexed { index, descendant ->
      val isLeaf = index == nodes.lastIndex - 1 /* -1 because we are dropping a node */
      ancestor = insert(ancestor, descendant, isLeaf)
    }
  }

  private fun insert(parent: Node<T>, value: T, isLeaf: Boolean): Node<T> {
    val existingChild = parent.findChild(value)
    if (existingChild != null) return existingChild
    return parent.insertChild(value, isLeaf)
  }

  fun <I : Comparable<I>, O> transform(transformer: NodeTransformer<Node<I>, O>): O =
    transformer.create(root as Node<I>)

  data class Node<T : Comparable<T>>(
    val value: T,
    private val mutableChildren: MutableList<Node<T>> = mutableListOf()
  ) {
    val children: List<Node<T>>
      get() = mutableChildren.toList()

    internal fun insertChild(value: T, isLeaf: Boolean): Node<T> {
      val child = Node(value)
      mutableChildren.add(findInsertionIndex(!isLeaf, child.value), child)
      return child
    }

    private fun findInsertionIndex(isBranch: Boolean, value: T): Int {
      return if (isBranch) {
        mutableChildren
          .indexOfFirst { sibling -> sibling.children.isNotEmpty() && sibling.value > value }
      } else {
        if (mutableChildren.size == 1) {
          if (mutableChildren.first().value < value) 1 else 0
        } else {
          val insertionIndex = mutableChildren.indexOfFirst { sibling -> sibling.children.isEmpty() && sibling.value > value }
          val hasBranchesButNotLeaves = insertionIndex == -1 && mutableChildren.isNotEmpty()
          if (hasBranchesButNotLeaves) {
            mutableChildren.size
          } else {
            insertionIndex
          }
        }
      }.coerceAtLeast(0)
    }

    fun findChild(value: T): Node<T>? {
      return mutableChildren.find { it.value == value }
    }
  }
}
