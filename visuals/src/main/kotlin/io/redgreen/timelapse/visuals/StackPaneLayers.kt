package io.redgreen.timelapse.visuals

import javafx.scene.Node
import javafx.scene.layout.StackPane

class StackPaneLayers<L>(
  private val stackPane: StackPane
) {
  private val layersAndNodes = mutableMapOf<L, Node>()

  fun show(layer: L) {
    layersAndNodes[layer]?.let {
      it.isVisible = true
    }

    (layersAndNodes.keys - layer)
      .onEach { layerKey -> layersAndNodes[layerKey]?.isVisible = false }
  }

  fun setLayer(layer: L, node: Node) {
    if (!stackPane.children.contains(node)) {
      throw IllegalArgumentException("Add the node as a child of the managed `StackPane` before setting it as a layer.")
    }
    layersAndNodes[layer] = node
  }
}
