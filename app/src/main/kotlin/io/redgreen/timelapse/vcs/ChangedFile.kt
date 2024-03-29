package io.redgreen.timelapse.vcs

@Deprecated("No longer in use. Please use [AffectedFile] instead")
sealed class ChangedFile(open val filePath: String) {
  data class Addition(override val filePath: String) : ChangedFile(filePath)
  data class Modification(override val filePath: String) : ChangedFile(filePath)
  data class Deletion(override val filePath: String) : ChangedFile(filePath)
  data class Rename(override val filePath: String, val oldFilePath: String) : ChangedFile(filePath)
}
