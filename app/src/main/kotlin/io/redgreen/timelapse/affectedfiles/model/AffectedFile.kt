package io.redgreen.timelapse.affectedfiles.model

sealed class AffectedFile(open val filePath: String) {
  abstract val changeCount: Int

  val filename: String by lazy {
    filePath.substring(filePath.lastIndexOf('/') + 1)
  }

  val directoryPath: String by lazy {
    val path = filePath.substring(0, filePath.lastIndexOf('/') + 1)
    if (path == "") "/" else path
  }

  data class New(
    override val filePath: String,
    val insertions: Int
  ) : AffectedFile(filePath) {
    override val changeCount: Int
      get() = insertions
  }

  data class Modified(
    override val filePath: String,
    val deletions: Int,
    val insertions: Int
  ) : AffectedFile(filePath) {
    override val changeCount: Int
      get() = deletions + insertions
  }

  data class Moved(
    override val filePath: String,
    val deletions: Int,
    val insertions: Int
  ) : AffectedFile(filePath) {
    override val changeCount: Int
      get() = deletions + insertions
  }

  data class Deleted(
    override val filePath: String,
    val deletions: Int
  ) : AffectedFile(filePath) {
    override val changeCount: Int
      get() = deletions
  }
}
