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

  private var currentSnapshot: String = seedText

  fun extendedDiff(patch: String): ExtendedDiff {
    val patchedText = applyPatch(currentSnapshot, patch)
    if (patchedText == currentSnapshot) {
      return NoChanges(currentSnapshot)
    }
    val comparisonResults = compare(currentSnapshot, patchedText, KotlinFunctionScanner)
    currentSnapshot = patchedText
    return HasChanges(patchedText, comparisonResults)
  }
}
