package io.redgreen.timelapse.extendeddiff

import io.redgreen.timelapse.extendeddiff.ExtendedDiff.NoChanges

class ExtendedDiffEngine private constructor(private val seedText: String) {
  companion object {
    fun newInstance(seedText: String): ExtendedDiffEngine {
      return ExtendedDiffEngine(seedText)
    }
  }

  fun extendedDiff(patch: String): Any {
    patch.length // To satisfy the linter
    return NoChanges(seedText)
  }
}
