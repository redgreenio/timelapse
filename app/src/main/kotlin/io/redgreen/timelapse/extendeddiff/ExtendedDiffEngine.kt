package io.redgreen.timelapse.extendeddiff

import io.redgreen.scout.FunctionScanner
import io.redgreen.timelapse.extendeddiff.ExtendedDiff.HasChanges
import io.redgreen.timelapse.extendeddiff.ExtendedDiff.NoChanges

class ExtendedDiffEngine private constructor(
  seedText: String,
  private val scanner: FunctionScanner
) {
  companion object {
    fun newInstance(seedText: String, scanner: FunctionScanner): ExtendedDiffEngine {
      return ExtendedDiffEngine(seedText, scanner)
    }
  }

  private var currentSnapshot: String = seedText

  fun extendedDiff(patch: String): ExtendedDiff {
    val patchedText = applyPatch(currentSnapshot, patch)
    if (patchedText == currentSnapshot) {
      return NoChanges(currentSnapshot)
    }
    val comparisonResults = compare(currentSnapshot, patchedText, scanner)
    currentSnapshot = patchedText
    return HasChanges(patchedText, comparisonResults)
  }
}
