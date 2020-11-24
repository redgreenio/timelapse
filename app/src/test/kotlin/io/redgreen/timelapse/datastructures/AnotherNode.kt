package io.redgreen.timelapse.datastructures

import io.redgreen.timelapse.datastructures.Tree.Node

data class AnotherNode(
  val value: Int,
  val children: MutableList<AnotherNode> = mutableListOf()
) {
  companion object {
    val transformer = object : NodeTransformer<Node<String>, AnotherNode> {
      override fun create(node: Node<String>): AnotherNode =
        AnotherNode(stringToInt(node), node.children.map(::create).toMutableList())
    }

    private fun stringToInt(node: Node<String>): Int {
      return when (node.value) {
        "root" -> 1
        "app" -> 2
        "main" -> 3
        "test" -> 4
        else -> throw UnsupportedOperationException("Unknown node value: ${node.value}")
      }
    }
  }
}
