package io.redgreen.timelapse.extendeddiff

sealed class ExtendedDiff {
  data class NoChanges(val text: String) : ExtendedDiff()

  data class HasChanges(
    val text: String,
    val comparisonResults: List<ComparisonResult>
  ) : ExtendedDiff()
}
