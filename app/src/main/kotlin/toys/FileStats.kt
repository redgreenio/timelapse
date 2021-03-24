@file:Suppress("MaxLineLength")

package toys

import com.google.gson.Gson
import toys.FileSystemNode.Directory
import toys.FileSystemNode.TextFile
import java.io.File
import java.io.FileNotFoundException

private val exclusions = listOf(
  ".icns",
  ".ico",
  ".png",
  ".ttf",
  ".jar",
  ".aar",
  ".so",
  ".mp3",
  ".gif",
  ".jpg",
  ".otf",
  ".dll",
  ".apk",
  ".pdf",
  ".bmp",
)

fun main() {
  val isGitHubProject = true
  val projectName = "intellij-community" // /Users/ragunathjawahar/GitHubProjects/intellij-community/"platform/platform-tests/testData/diff/applyPatch/fileWithGitStyleCyrillicPaths/
  val jsonOutFile = "/Users/ragunathjawahar/JsProjects/file-stats/files/data.json"

  val projectContainerDirectory = if (isGitHubProject) "GitHubProjects" else "IdeaProjects"
  val gitDirectory = File("/Users/ragunathjawahar/$projectContainerDirectory/$projectName")

  val filesInCurrentRevision = getFilesInCurrentRevision("${gitDirectory.absolutePath}/.git")

  /*
  val extensions = filesInCurrentRevision
    .map { if (it.contains('.')) it.substring(it.lastIndexOf('.')) else it }
    .distinct()
  */

  val filteredFiles = filesInCurrentRevision
    .filter { !it.contains('.') || !exclusions.contains(it.substring(it.lastIndexOf('.'))) }

  println("Total files: ${filteredFiles.size}")

  val rootDirectory = Directory(".")

  filteredFiles
    .onEach {
      val pathInGitRepository = "$projectName/$it"
      val lineCount = try {
        countLines("${gitDirectory.absolutePath}/$it")
      } catch (e: FileNotFoundException) {
        e.printStackTrace()
        0
      }

      val splitPath = pathInGitRepository.split('/')
      val filename = splitPath.last()
      val directoryNames = splitPath.dropLast(1)

      var currentDirectory = rootDirectory
      directoryNames.forEach { directoryName ->
        val directory = currentDirectory
          .children
          .find { child -> child is Directory && child.name == directoryName } as Directory?

        currentDirectory = if (directory != null) {
          directory
        } else {
          val newDirectory = Directory(directoryName)
          currentDirectory.children.add(newDirectory)
          newDirectory
        }
      }

      val textFile = TextFile(filename, lineCount)
      currentDirectory.children.add(textFile)
    }

  val json = Gson().toJson(rootDirectory.children.first())
  File(jsonOutFile).writeText(json)
}

private fun countLines(path: String): Int {
  return File(path).readText().split('\n').size
}

sealed class FileSystemNode {
  data class Directory(
    val name: String,
    val children: MutableList<FileSystemNode> = mutableListOf()
  ) : FileSystemNode()

  data class TextFile(
    val name: String,
    val value: Int
  ) : FileSystemNode()
}
