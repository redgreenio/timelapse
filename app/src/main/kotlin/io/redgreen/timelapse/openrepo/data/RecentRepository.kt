package io.redgreen.timelapse.openrepo.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.redgreen.timelapse.foo.fastLazy
import java.io.File

@JsonClass(generateAdapter = true)
data class RecentRepository(
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

  val title: String by fastLazy {
    path
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
