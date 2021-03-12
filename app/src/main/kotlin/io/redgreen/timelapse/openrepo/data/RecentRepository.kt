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

  val title: String by fastLazy {
    if (path.endsWith(GIT_DIRECTORY)) {
      path.split(File.separatorChar).dropLast(1).last()
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
    return if (path.endsWith(GIT_DIRECTORY)) {
      path
        .replace(userHomeDirectoryPath, replacement)
        .split(File.separatorChar)
        .dropLast(1)
        .joinToString("${File.separatorChar}")
    } else {
      path.replace(userHomeDirectoryPath, replacement)
    }
  }
}
