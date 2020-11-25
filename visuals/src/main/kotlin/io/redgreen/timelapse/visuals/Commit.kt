package io.redgreen.timelapse.visuals

data class Commit(
  val insertions: Int,
  val deletions: Int = 0
)
