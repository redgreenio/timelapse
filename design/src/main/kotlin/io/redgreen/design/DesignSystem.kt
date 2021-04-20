package io.redgreen.design

import javafx.scene.Scene

object DesignSystem {
  private val cssFiles = listOf(
    "fonts.css",
    "titled-parent.css"
  )

  fun initialize(scene: Scene) {
    cssFiles
      .map { "/css/$it" }
      .map { DesignSystem::class.java.getResource(it).toExternalForm() }
      .onEach { scene.stylesheets.add(it) }
  }
}
