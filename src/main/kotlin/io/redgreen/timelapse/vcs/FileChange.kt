package io.redgreen.timelapse.vcs

sealed class FileChange(open val filePath: String) {
  data class Addition(override val filePath: String) : FileChange(filePath)
  data class Modification(override val filePath: String) : FileChange(filePath)
  data class Deletion(override val filePath: String) : FileChange(filePath)
  data class Rename(override val filePath: String, val oldFilePath: String) : FileChange(filePath)
}
