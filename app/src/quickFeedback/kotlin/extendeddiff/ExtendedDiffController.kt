package extendeddiff

import javafx.fxml.FXML
import javafx.scene.web.WebView

class ExtendedDiffController {
  @FXML
  private lateinit var codeViewerWebView: WebView

  fun start() {
    codeViewerWebView.engine.loadContent("<b>Hello, world!</b>")
  }
}
