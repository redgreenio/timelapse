package io.redgreen.timelapse.vcs

@Deprecated("No longer in use. Please use [AffectedFile] instead")
sealed class ChangedFile(open val filePath: String) {
  @Deprecated("No longer in use. Please use [AffectedFile.Added] instead")
  data class Addition(override val filePath: String) : ChangedFile(filePath)

  @Deprecated("No longer in use. Please use [AffectedFile.Modified] instead")
  data class Modification(override val filePath: String) : ChangedFile(filePath)

  @Deprecated("No longer in use. Please use [AffectedFile.Deleted] instead")
  data class Deletion(override val filePath: String) : ChangedFile(filePath)

  @Deprecated("No longer in use. Please use [AffectedFile.Moved] instead")
  data class Rename(override val filePath: String, val oldFilePath: String) : ChangedFile(filePath)
}
