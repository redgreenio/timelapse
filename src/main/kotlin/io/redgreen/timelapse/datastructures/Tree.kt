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
    for (descendant in nodes.drop(1)) {
      ancestor = insert(ancestor, descendant)
    }
  }

  private fun insert(parent: Node<T>, value: T): Node<T> {
    val existingChild = parent.findChild(value)
    if (existingChild != null) return existingChild
    return parent.addChild(value)
  }

  fun <I : Any, O> transform(transformer: NodeTransformer<Node<I>, O>): O =
    transformer.create(root as Node<I>)

  data class Node<T>(
    val value: T,
    private val mutableChildren: MutableList<Node<T>> = mutableListOf()
  ) {
    val children: List<Node<T>>
      get() = mutableChildren.toList()

    internal fun addChild(value: T): Node<T> {
      val child = Node(value)
      mutableChildren.add(child)
      return child
    }

    fun findChild(value: T): Node<T>? {
      return mutableChildren.find { it.value == value }
    }
  }
}
