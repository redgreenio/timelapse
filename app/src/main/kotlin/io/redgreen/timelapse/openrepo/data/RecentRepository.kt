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
  }

  val title: String by fastLazy {
    path.substring(path.lastIndexOf(File.separatorChar) + 1)
  }

  fun subtitle(userHomeDirectory: String): String {
    val replacement = if (userHomeDirectory.endsWith(File.separatorChar)) "$TILDE${File.separatorChar}" else TILDE
    return path.replace(userHomeDirectory, replacement)
  }
}
