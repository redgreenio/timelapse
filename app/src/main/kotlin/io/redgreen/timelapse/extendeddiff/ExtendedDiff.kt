package io.redgreen.timelapse.extendeddiff

sealed class ExtendedDiff {
  data class NoChanges(val sourceCode: String) : ExtendedDiff()

  data class HasChanges(
    val sourceCode: String,
    val comparisonResults: List<ComparisonResult>
  ) : ExtendedDiff()
}
