package io.redgreen.timelapse.datastructures

class Tree<T> private constructor(
  val root: Node<T>,
  private val nodeSplitter: (T) -> List<T>
) {
  companion object {
    fun <T> create(root: T, split: (T) -> List<T>): Tree<T> {
      return Tree(Node(root), split)
    }
  }

  fun insert(value: T) {
    val nodes = nodeSplitter(value)

    var ancestor = root
    nodes.drop(1).forEachIndexed { index, descendant ->
      val isLeaf = index == nodes.lastIndex - 1 /* -1 because we are dropping a node */
      println("$descendant: index: $index leaf: ${isLeaf}, nodes: $nodes") // TODO: 07/02/21 Remove this line
      ancestor = insert(ancestor, descendant, isLeaf)
    }
  }

  private fun insert(parent: Node<T>, value: T, isLeaf: Boolean): Node<T> {
    val existingChild = parent.findChild(value)
    if (existingChild != null) return existingChild
    return parent.insertChild(value, isLeaf)
  }

  fun <I : Any, O> transform(transformer: NodeTransformer<Node<I>, O>): O =
    transformer.create(root as Node<I>)

  data class Node<T>(
    val value: T,
    private val mutableChildren: MutableList<Node<T>> = mutableListOf()
  ) {
    val children: List<Node<T>>
      get() = mutableChildren.toList()

    internal fun insertChild(value: T, isLeaf: Boolean): Node<T> {
      val child = Node(value)
      if (isLeaf) {
        mutableChildren.add(child)
      } else {
        val insertionIndex = mutableChildren.indexOfFirst { it.children.isEmpty() }.coerceAtLeast(0)
        mutableChildren.add(insertionIndex, child)
      }
      return child
    }

    fun findChild(value: T): Node<T>? {
      return mutableChildren.find { it.value == value }
    }
  }
}
