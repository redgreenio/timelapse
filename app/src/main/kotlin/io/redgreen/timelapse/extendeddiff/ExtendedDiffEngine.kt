package io.redgreen.timelapse.extendeddiff

import io.redgreen.scout.FunctionScanner
import io.redgreen.timelapse.extendeddiff.ExtendedDiff.HasChanges
import io.redgreen.timelapse.extendeddiff.ExtendedDiff.NoChanges

class ExtendedDiffEngine private constructor(
  seedSourceCode: String,
  private val scanner: FunctionScanner
) {
  companion object {
    fun newInstance(seedSourceCode: String, scanner: FunctionScanner): ExtendedDiffEngine {
      return ExtendedDiffEngine(seedSourceCode, scanner)
    }
  }

  private var currentSourceSnapshot: String = seedSourceCode

  fun extendedDiff(patch: String): ExtendedDiff {
    val patchedText = applyPatch(currentSourceSnapshot, patch)
    if (patchedText == currentSourceSnapshot) {
      return NoChanges(currentSourceSnapshot)
    }
    val comparisonResults = compare(currentSourceSnapshot, patchedText, scanner)
    currentSourceSnapshot = patchedText
    return HasChanges(patchedText, comparisonResults)
  }
}
