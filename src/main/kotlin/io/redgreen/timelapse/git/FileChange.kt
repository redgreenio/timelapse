package io.redgreen.timelapse.git

sealed class FileChange {
  data class Addition(val filePath: String) : FileChange()
  data class Modification(val filePath: String) : FileChange()
  data class Deletion(val filePath: String) : FileChange()
  data class Rename(val filePath: String, val oldFilePath: String) : FileChange()
}
