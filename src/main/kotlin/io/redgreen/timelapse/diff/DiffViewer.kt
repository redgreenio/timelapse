package io.redgreen.timelapse.diff

import javafx.scene.layout.StackPane
import javafx.scene.web.WebView

class DiffViewer : StackPane() {
  private val webView = WebView()

  init {
    children.add(webView)
  }

  // TODO: 14-11-2020 Change this to accept a formatted diff object
  fun showContent(html: String) {
    webView.engine.loadContent(html)
  }
}
