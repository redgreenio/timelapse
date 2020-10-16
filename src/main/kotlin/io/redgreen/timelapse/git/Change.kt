package io.redgreen.timelapse.git

sealed class Change(open val filePath: String) {
  data class Addition(override val filePath: String) : Change(filePath)
  data class Modification(override val filePath: String) : Change(filePath)
  data class Deletion(override val filePath: String) : Change(filePath)
  data class Rename(override val filePath: String, val oldFilePath: String) : Change(filePath)
}
