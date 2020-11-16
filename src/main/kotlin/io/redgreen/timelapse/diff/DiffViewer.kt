package io.redgreen.timelapse.diff

import io.redgreen.timelapse.foo.fastLazy
import javafx.scene.layout.StackPane
import javafx.scene.web.WebView

class DiffViewer : StackPane() {
  private val webView by fastLazy { WebView() }

  init {
    children.add(webView)
  }

  fun showDiff(diff: FormattedDiff) {
    webView.engine.loadContent(diff.toHtml())
  }
}
