package io.redgreen.timelapse.affectedfiles.model

import io.redgreen.timelapse.core.TrackedFilePath

sealed class AffectedFile(
  open val filePath: TrackedFilePath
) {
  abstract val changeCount: Int

  val filename: String by lazy {
    filePath.value.substring(filePath.value.lastIndexOf('/') + 1)
  }

  // TODO: 04/03/21 consider moving this to the presentation layer or introduce a new type
  val directoryPath: String by lazy {
    val path = filePath.value.substring(0, filePath.value.lastIndexOf('/') + 1)
    if (path == "") "/" else path
  }

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
