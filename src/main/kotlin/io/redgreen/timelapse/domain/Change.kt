package io.redgreen.timelapse.domain

data class Change(
  val commitId: String,
  val message: String,
  val insertions: Int = 0,
  val deletions: Int = 0,
)
