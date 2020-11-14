package io.redgreen.timelapse.diff

import javafx.scene.layout.StackPane
import javafx.scene.web.WebView

class DiffViewer : StackPane() {
  private val webView = WebView()

  init {
    children.add(webView)
  }

  fun showDiff(diff: FormattedDiff) {
    webView.engine.loadContent(diff.toHtml())
  }
}
