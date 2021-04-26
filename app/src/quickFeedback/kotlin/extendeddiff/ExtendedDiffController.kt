package extendeddiff

import extendeddiff.samplecreator.Sample
import extendeddiff.samplecreator.Samples
import io.redgreen.scout.languages.kotlin.KotlinFunctionScanner
import io.redgreen.timelapse.extendeddiff.ExtendedDiffEngine
import io.redgreen.timelapse.extendeddiff.toHtml
import javafx.fxml.FXML
import javafx.scene.web.WebView

class ExtendedDiffController {
  @FXML
  private lateinit var codeViewerWebView: WebView

  fun start() {
    val sample = Samples.EXTENDED_DIFF
    val seedSourceCode = readResourceFile("/samples/${sample.name}/seed.txt")
    val diffEngine = ExtendedDiffEngine.newInstance(seedSourceCode, KotlinFunctionScanner)
    val seedSourceCodeExtendedDiff = diffEngine.extendedDiff("")

    val html = seedSourceCodeExtendedDiff.toHtml()
    renderHtml(html)
  }

  private fun renderHtml(html: String) {
    codeViewerWebView.engine.loadContent(html)
  }

  private fun readResourceFile(resourcePath: String): String {
    return Sample::class.java
      .getResourceAsStream(resourcePath)
      .reader()
      .readText()
  }
}
