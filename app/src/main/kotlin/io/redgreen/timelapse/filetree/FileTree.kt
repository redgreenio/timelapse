package io.redgreen.timelapse.filetree

class FileTree(
  private val rootDirectoryPath: String,
  private val filePaths: List<String>,
) {
  companion object {
    private const val NEWLINE = "\n"
    private const val SEPARATOR = '/'

    fun create(
      rootDirectoryPath: String,
      filePaths: List<String> = emptyList()
    ): FileTree {
      return FileTree(rootDirectoryPath, filePaths)
    }
  }

  override fun toString(): String {
    val displayTreeBuilder = StringBuilder().apply { append(rootDirectoryPath) }
    val maxLevel = filePaths.maxOfOrNull { it.count { filePathChar -> filePathChar == SEPARATOR } } ?: 0

    var level = 0
    while (level < maxLevel || maxLevel == 0) {
      val currentLevelFilePaths = files(level)
      val currentLevelDirectories = directories(level)
      val nextLevelFilePaths = files(++level)

      for (directoryPath in currentLevelDirectories) {
        displayTreeBuilder.append(NEWLINE)
        displayTreeBuilder.append("├─ ")
        displayTreeBuilder.append(directoryPath)

        val directoryFilePaths = nextLevelFilePaths
          .filter { it.startsWith(directoryPath) }

        directoryFilePaths
          .forEachIndexed { index, filePath ->
            displayTreeBuilder.append(NEWLINE)
            if (index != directoryFilePaths.lastIndex) {
              displayTreeBuilder.append("│  ├─ ")
            } else {
              displayTreeBuilder.append("│  └─ ")
            }
            displayTreeBuilder.append(filePath.split(SEPARATOR).last())
          }
      }

      if (currentLevelFilePaths.isNotEmpty()) {
        currentLevelFilePaths.forEachIndexed { index, filePath ->
          displayTreeBuilder.append(NEWLINE)
          if (index != currentLevelFilePaths.lastIndex) {
            displayTreeBuilder.append("├─ ")
          } else {
            displayTreeBuilder.append("└─ ")
          }
          displayTreeBuilder.append(filePath)
        }
      }

      if (maxLevel == 0) {
        break
      }
    }

    return displayTreeBuilder.toString()
  }

  private fun files(level: Int): List<String> {
    return filePaths
      .filter { filePath -> filePath.count { filePathChar -> filePathChar == SEPARATOR } == level }
  }

  private fun directories(level: Int): List<String> {
    return files(level + 1)
      .map { it.split(SEPARATOR) }
      .map(List<String>::first)
      .distinct()
  }
}
