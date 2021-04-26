package extendeddiff

import extendeddiff.samplecreator.Sample
import extendeddiff.samplecreator.Samples
import io.redgreen.scout.languages.kotlin.KotlinFunctionScanner
import io.redgreen.timelapse.extendeddiff.ExtendedDiffEngine
import io.redgreen.timelapse.extendeddiff.toHtml
import io.redgreen.timelapse.foo.fastLazy
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.web.WebView

class ExtendedDiffController {
  companion object {
    private const val PATCH_FOR_SEED_SOURCE = ""
  }

  @FXML
  private lateinit var codeViewerWebView: WebView

  @FXML
  private lateinit var nextButton: Button

  @FXML
  private lateinit var patchCountLabel: Label

  private var patchCount = 0
  private val sample = Samples.EXTENDED_DIFF

  private val diffEngine by fastLazy {
    val seedSourceCode = readResourceFile("/samples/${sample.name}/seed.txt")
    ExtendedDiffEngine.newInstance(seedSourceCode, KotlinFunctionScanner)
  }

  fun start() {
    val seedSourceCodeExtendedDiff = diffEngine.extendedDiff(PATCH_FOR_SEED_SOURCE)
    renderHtml(seedSourceCodeExtendedDiff.toHtml())

    nextButton.setOnAction {
      patchCount++
      applyNextPatch(patchCount)
      updatePatchCountLabel(patchCount, sample.patchesCount)
      disableButtonOnLastPatch(patchCount, sample.patchesCount)
    }
  }

  private fun applyNextPatch(patchCount: Int) {
    val patchFileName = String.format("%02d.patch", patchCount)
    val patchToApply = readResourceFile("/samples/${sample.name}/$patchFileName")
    val nextHtml = diffEngine.extendedDiff(patchToApply).toHtml()
    renderHtml(nextHtml)
  }

  private fun updatePatchCountLabel(patchCount: Int, totalPatches: Int) {
    patchCountLabel.text = String.format("%d/%d", patchCount, totalPatches)
  }

  private fun disableButtonOnLastPatch(patchCount: Int, totalPatches: Int) {
    val isLastPatch = patchCount == totalPatches
    if (isLastPatch) {
      nextButton.isDisable = true
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
