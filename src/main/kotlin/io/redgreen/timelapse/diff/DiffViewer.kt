package io.redgreen.timelapse.diff

import javafx.scene.layout.StackPane
import javafx.scene.web.WebView
import kotlin.LazyThreadSafetyMode.NONE

class DiffViewer : StackPane() {
  private val webView by lazy(NONE) { WebView() }

  init {
    children.add(webView)
  }

  fun showDiff(diff: FormattedDiff) {
    webView.engine.loadContent(diff.toHtml())
  }
}
