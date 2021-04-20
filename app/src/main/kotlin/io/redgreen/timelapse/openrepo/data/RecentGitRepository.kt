package io.redgreen.timelapse.openrepo.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.File

/**
 * Represents the path to a recently opened Git directory. The path should end with '.git', otherwise the
 * constructor will throw an exception.
 */
@JsonClass(generateAdapter = true)
data class RecentGitRepository(
  @Json(name = "path") val path: String
) {
  companion object {
    private const val TILDE = "~"
    private const val GIT_DIRECTORY = ".git"
    private val SEPARATOR_CHAR = File.separatorChar
  }

  init {
    require(isGitDirectory(path)) { "Path should end with a '.git' directory, but was: $path" }
  }

  fun title(): String {
    return path
      .split(SEPARATOR_CHAR)
      .filter { it.isNotEmpty() }
      .dropLast(1)
      .last()
  }

  fun subtitle(userHomeDirectoryPath: String = System.getProperty("user.home")): String {
    val pathPrefix = getPrefixBasedOn(userHomeDirectoryPath)
    val segmentsToDrop = if (path.endsWith(SEPARATOR_CHAR)) 2 else 1
    return path
      .replace(userHomeDirectoryPath, pathPrefix)
      .split(SEPARATOR_CHAR)
      .dropLast(segmentsToDrop)
      .joinToString("$SEPARATOR_CHAR")
  }

  private fun isGitDirectory(path: String): Boolean {
    return path.endsWith(GIT_DIRECTORY) ||
      path.endsWith("$GIT_DIRECTORY$SEPARATOR_CHAR")
  }

  private fun getPrefixBasedOn(userHomeDirectoryPath: String): String {
    return if (userHomeDirectoryPath.endsWith(SEPARATOR_CHAR)) {
      "$TILDE$SEPARATOR_CHAR"
    } else {
      TILDE
    }
  }
}
