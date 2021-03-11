package io.redgreen.timelapse.git.model

sealed class AffectedFile(
  open val filePath: TrackedFilePath
) {
  abstract val changeCount: Int

  data class Added(
    override val filePath: TrackedFilePath,
    val insertions: Int
  ) : AffectedFile(filePath) {
    override val changeCount: Int
      get() = insertions
  }

  data class Modified(
    override val filePath: TrackedFilePath,
    val deletions: Int,
    val insertions: Int
  ) : AffectedFile(filePath) {
    override val changeCount: Int
      get() = deletions + insertions
  }

  data class Moved constructor(
    override val filePath: TrackedFilePath,
    val oldFilePath: TrackedFilePath,
    val deletions: Int,
    val insertions: Int
  ) : AffectedFile(filePath) {
    override val changeCount: Int
      get() = deletions + insertions
  }

  data class Deleted(
    override val filePath: TrackedFilePath,
    val deletions: Int
  ) : AffectedFile(filePath) {
    override val changeCount: Int
      get() = deletions
  }
}
