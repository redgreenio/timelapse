package extendeddiff

import extendeddiff.samplecreator.Samples
import io.redgreen.scout.languages.kotlin.KotlinFunctionScanner
import io.redgreen.timelapse.extendeddiff.ComparisonResult
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Added
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Deleted
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Modified
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Unmodified
import io.redgreen.timelapse.extendeddiff.ExtendedDiff
import io.redgreen.timelapse.extendeddiff.ExtendedDiff.HasChanges
import io.redgreen.timelapse.extendeddiff.ExtendedDiffEngine
import io.redgreen.timelapse.extendeddiff.toHtml
import io.redgreen.timelapse.foo.readResourceText
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

  @FXML
  private lateinit var functionStatsLabel: Label

  private val sample = Samples.EXTENDED_DIFF
  private var patchCount = sample.patchCountOffset

  private val seedSourceCode = readResourceText(sampleResourcePath(sample.name, SEED_FILE))
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
    patchCount = sample.patchCountOffset
    resetButton.isDisable = true
    nextButton.isDisable = false
    patchCountLabel.text = ""

    diffEngine = ExtendedDiffEngine.newInstance(seedSourceCode, KotlinFunctionScanner)
    val seedSourceCodeExtendedDiff = diffEngine.extendedDiff(PATCH_FOR_SEED_SOURCE)
    displayDiff(seedSourceCodeExtendedDiff)
  }

  private fun applyNextPatch(patchCount: Int) {
    val patchFileName = String.format(FORMAT_PATCH_FILE, patchCount)
    val patchToApply = readResourceText(sampleResourcePath(sample.name, patchFileName))
    val extendedDiff = diffEngine.extendedDiff(patchToApply)
    displayDiff(extendedDiff)
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

  private fun displayDiff(extendedDiff: ExtendedDiff) {
    codeViewerWebView.engine.loadContent(extendedDiff.toHtml())
    if (extendedDiff is HasChanges) {
      functionStatsLabel.text = getStats(extendedDiff.comparisonResults)
    }
  }

  private fun sampleResourcePath(sample: String, fileName: String): String {
    return "/samples/$sample/$fileName"
  }

  private fun getStats(comparisonResults: List<ComparisonResult>): String {
    val addedCount = comparisonResults.filterIsInstance<Added>().count()
    val deletedCount = comparisonResults.filterIsInstance<Deleted>().count()
    val modifiedCount = comparisonResults.filterIsInstance<Modified>().count()
    val renamedCount = comparisonResults.filterIsInstance<ComparisonResult.Renamed>().count()
    val unmodifiedCount = comparisonResults.filterIsInstance<Unmodified>().count()

    return "$addedCount (Add), " +
      "$deletedCount (Del.), " +
      "$modifiedCount (Mod.), " +
      "$renamedCount (Ren.), " +
      "$unmodifiedCount (Unmod.)"
  }
}
