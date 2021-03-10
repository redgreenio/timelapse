package io.redgreen.timelapse.visuals

import javafx.scene.Node
import javafx.scene.layout.StackPane

class StackPaneLayers<L>(
  private val stackPane: StackPane,
  layersDefinition: StackPaneLayers<L>.() -> Unit
) {
  private val layersAndNodes = mutableMapOf<L, Node>()

  init {
    layersDefinition()
  }

  fun show(layer: L) {
    layersAndNodes[layer]?.let {
      it.isVisible = true
    }

    (layersAndNodes.keys - layer)
      .onEach { layerKey -> layersAndNodes[layerKey]?.isVisible = false }
  }

  infix fun Node.at(layer: L) {
    setLayer(layer, this)
  }

  private fun setLayer(layer: L, node: Node) {
    if (!stackPane.children.contains(node)) {
      stackPane.children.add(node)
    }
    layersAndNodes[layer] = node
  }
}
