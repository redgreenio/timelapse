package io.redgreen.timelapse.search

sealed class Occurrence {
  object None : Occurrence()

  data class Segment(
    val startIndex: Int,
    val chars: Int
  ) : Occurrence()
}
