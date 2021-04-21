package io.redgreen.timelapse.extendeddiff

sealed class ExtendedDiff {
  data class NoChanges(val text: String) : ExtendedDiff()

  data class HasChanges(
    val sourceCode: String,
    val comparisonResults: List<ComparisonResult>
  ) : ExtendedDiff()
}
