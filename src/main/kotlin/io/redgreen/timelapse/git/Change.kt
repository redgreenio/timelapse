package io.redgreen.timelapse.git

sealed class Change {
  data class Addition(val filePath: String) : Change()
  data class Modification(val filePath: String) : Change()
  data class Deletion(val filePath: String) : Change()
  data class Rename(val filePath: String, val oldFilePath: String) : Change()
}
