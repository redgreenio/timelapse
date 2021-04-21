package io.redgreen.timelapse.extendeddiff

import io.redgreen.scout.languages.kotlin.KotlinFunctionScanner
import io.redgreen.timelapse.extendeddiff.ExtendedDiff.HasChanges
import io.redgreen.timelapse.extendeddiff.ExtendedDiff.NoChanges

class ExtendedDiffEngine private constructor(private val seedText: String) {
  companion object {
    fun newInstance(seedText: String): ExtendedDiffEngine {
      return ExtendedDiffEngine(seedText)
    }
  }

  fun extendedDiff(patch: String): ExtendedDiff {
    val patchedText = applyPatch(seedText, patch)
    if (patchedText == seedText) {
      return NoChanges(seedText)
    }
    val comparisonResults = compare(seedText, patchedText, KotlinFunctionScanner)
    return HasChanges(patchedText, comparisonResults)
  }
}
