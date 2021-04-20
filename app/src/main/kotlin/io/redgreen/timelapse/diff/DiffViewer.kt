package io.redgreen.timelapse.diff

import io.redgreen.timelapse.foo.fastLazy
import javafx.scene.layout.StackPane
import javafx.scene.text.Font
import javafx.scene.web.WebView

class DiffViewer : StackPane() {
  companion object {
    private const val DEFAULT_FONT = "/fonts/fira-code/FiraCode-Regular.ttf"
    private const val DEFAULT_FONT_SIZE = 13.0
  }

  internal val webView by fastLazy {
    DiffViewer::class.java.getResourceAsStream(DEFAULT_FONT).use {
      Font.loadFont(it, DEFAULT_FONT_SIZE)
    }
    WebView()
  }

  init {
    children.add(webView)
  }

  fun showDiff(diff: FormattedDiff) {
    webView.engine.loadContent(diff.toHtml())
  }
}
