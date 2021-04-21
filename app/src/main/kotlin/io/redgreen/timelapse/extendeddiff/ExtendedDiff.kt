package io.redgreen.timelapse.extendeddiff

sealed class ExtendedDiff {
  data class NoChanges(val text: String) : ExtendedDiff()
}
