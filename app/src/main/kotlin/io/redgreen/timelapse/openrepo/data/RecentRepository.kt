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
    val endsWithGitDirectory = isGitDirectory(path)
    require(endsWithGitDirectory) { "Path should end with a '.git' directory, but was: $path" }
  }

  val title: String by fastLazy {
    if (isGitDirectory(path)) {
      path.split(File.separatorChar).filter { it.isNotEmpty() }.dropLast(1).last()
    } else {
      path.substring(path.lastIndexOf(File.separatorChar) + 1)
    }
  }

  fun subtitle(userHomeDirectoryPath: String): String {
    val replacement = if (userHomeDirectoryPath.endsWith(File.separatorChar)) {
      "$TILDE${File.separatorChar}"
    } else {
      TILDE
    }
    return if (isGitDirectory(path)) {
      val segmentsToDrop = if (path.endsWith(File.separatorChar)) 2 else 1
      path
        .replace(userHomeDirectoryPath, replacement)
        .split(File.separatorChar)
        .dropLast(segmentsToDrop)
        .joinToString("${File.separatorChar}")
    } else {
      path.replace(userHomeDirectoryPath, replacement)
    }
  }

  private fun isGitDirectory(path: String): Boolean {
    return path.endsWith(GIT_DIRECTORY) ||
      path.endsWith("$GIT_DIRECTORY${File.separatorChar}")
  }
}
