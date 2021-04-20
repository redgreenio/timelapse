package io.redgreen.timelapse.domain

sealed class Change(
  open val commitId: String,
  open val message: String,
  open val insertions: Int,
  open val deletions: Int,
) {
  data class Modification(
    override val commitId: String,
    override val message: String,
    override val insertions: Int = 0,
    override val deletions: Int = 0,
  ) : Change(commitId, message, insertions, deletions)

  data class Move(
    override val commitId: String,
    override val message: String,
    override val insertions: Int = 0,
    override val deletions: Int = 0,
    val oldPathFragment: String,
    val newPathFragment: String,
  ) : Change(commitId, message, insertions, deletions)
}
