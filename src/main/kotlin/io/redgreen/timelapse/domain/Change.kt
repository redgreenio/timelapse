package io.redgreen.timelapse.domain

sealed class Change(
  open val commitId: String,
  open val message: String,
  open val insertions: Int = 0,
  open val deletions: Int = 0,
) {
  data class Modification(
    override val commitId: String,
    override val message: String,
    override val insertions: Int = 0,
    override val deletions: Int = 0,
  ) : Change(commitId, message, insertions, deletions)
}
