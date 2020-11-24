package io.redgreen.timelapse.datastructures

import io.redgreen.timelapse.datastructures.Tree.Node

interface NodeTransformer<I : Node<out Any>, O> {
  fun create(node: I): O
}
