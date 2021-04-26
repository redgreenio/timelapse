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
  companion object {
    private const val SEED_FILE = "seed.txt"
    private const val PATCH_FOR_SEED_SOURCE = ""

    private const val FORMAT_PATCH_FILE = "%02d.patch"
    private const val FORMAT_PATCH_COUNT = "%d/%d"
  }

  @FXML
  private lateinit var codeViewerWebView: WebView

  @FXML
  private lateinit var nextButton: Button

  @FXML
  private lateinit var resetButton: Button

  @FXML
  private lateinit var patchCountLabel: Label

  private var patchCount = 0
  private val sample = Samples.EXTENDED_DIFF

  private val seedSourceCode = readResourceFile(sampleResourcePath(sample.name, SEED_FILE))
  private lateinit var diffEngine: ExtendedDiffEngine

  fun start() {
    showSeedSourceCode()

    nextButton.setOnAction {
      patchCount++
      applyNextPatch(patchCount)
      updatePatchCountLabel(patchCount, sample.patchesCount)
      disableNextButtonOnLastPatch(patchCount, sample.patchesCount)
      resetButton.isDisable = patchCount == 0
    }

    resetButton.setOnAction { showSeedSourceCode() }
  }

  private fun showSeedSourceCode() {
    patchCount = 0
    resetButton.isDisable = true
    nextButton.isDisable = false
    patchCountLabel.text = ""

    diffEngine = ExtendedDiffEngine.newInstance(seedSourceCode, KotlinFunctionScanner)
    val seedSourceCodeExtendedDiff = diffEngine.extendedDiff(PATCH_FOR_SEED_SOURCE)
    renderHtml(seedSourceCodeExtendedDiff.toHtml())
  }

  private fun applyNextPatch(patchCount: Int) {
    val patchFileName = String.format(FORMAT_PATCH_FILE, patchCount)
    val patchToApply = readResourceFile(sampleResourcePath(sample.name, patchFileName))
    val nextHtml = diffEngine.extendedDiff(patchToApply).toHtml()
    renderHtml(nextHtml)
  }

  private fun updatePatchCountLabel(patchCount: Int, totalPatches: Int) {
    patchCountLabel.text = String.format(FORMAT_PATCH_COUNT, patchCount, totalPatches)
  }

  private fun disableNextButtonOnLastPatch(patchCount: Int, totalPatches: Int) {
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

  private fun sampleResourcePath(sample: String, fileName: String): String {
    return "/samples/$sample/$fileName"
  }
}
