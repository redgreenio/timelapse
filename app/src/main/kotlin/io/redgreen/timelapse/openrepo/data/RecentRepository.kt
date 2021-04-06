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
  }

  init {
    require(isGitDirectory(path)) { "Path should end with a '.git' directory, but was: $path" }
  }

  val title: String by fastLazy {
    path
      .split(File.separatorChar)
      .filter { it.isNotEmpty() }
      .dropLast(1)
      .last()
  }

  fun subtitle(userHomeDirectoryPath: String): String {
    val pathPrefix = getPrefixBasedOn(userHomeDirectoryPath)
    val segmentsToDrop = if (path.endsWith(File.separatorChar)) 2 else 1
    return path
      .replace(userHomeDirectoryPath, pathPrefix)
      .split(File.separatorChar)
      .dropLast(segmentsToDrop)
      .joinToString("${File.separatorChar}")
  }

  private fun isGitDirectory(path: String): Boolean {
    return path.endsWith(GIT_DIRECTORY) ||
      path.endsWith("$GIT_DIRECTORY${File.separatorChar}")
  }

  private fun getPrefixBasedOn(userHomeDirectoryPath: String): String {
    return if (userHomeDirectoryPath.endsWith(File.separatorChar)) {
      "$TILDE${File.separatorChar}"
    } else {
      TILDE
    }
  }
}
