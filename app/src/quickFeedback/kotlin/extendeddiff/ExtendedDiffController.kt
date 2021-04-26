package extendeddiff

import extendeddiff.samplecreator.Sample
import extendeddiff.samplecreator.Samples
import io.redgreen.scout.languages.kotlin.KotlinFunctionScanner
import io.redgreen.timelapse.extendeddiff.ExtendedDiffEngine
import io.redgreen.timelapse.extendeddiff.toHtml
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.web.WebView

class ExtendedDiffController {
  @FXML
  private lateinit var codeViewerWebView: WebView

  @FXML
  private lateinit var nextButton: Button

  @FXML
  private lateinit var patchCountLabel: Label

  private var patchCount = 0
  private val sample = Samples.EXTENDED_DIFF

  fun start() {
    val seedSourceCode = readResourceFile("/samples/${sample.name}/seed.txt")
    val diffEngine = ExtendedDiffEngine.newInstance(seedSourceCode, KotlinFunctionScanner)
    val seedSourceCodeExtendedDiff = diffEngine.extendedDiff("")

    val html = seedSourceCodeExtendedDiff.toHtml()
    renderHtml(html)

    nextButton.setOnAction {
      patchCount++
      val patchFileName = String.format("%02d.patch", patchCount)
      val patchToApply = readResourceFile("/samples/${sample.name}/$patchFileName")
      val nextHtml = diffEngine.extendedDiff(patchToApply).toHtml()
      renderHtml(nextHtml)

      patchCountLabel.text = String.format("%d/%d", patchCount, sample.patchesCount)
    }
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
