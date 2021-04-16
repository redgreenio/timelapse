package io.redgreen.timelapse.gradle.automatedrelease

object Versioning {
  fun getNextVersion(
    yyyy: Int,
    version: String,
    isPublic: Boolean = true
  ): String {
    // Temporary fix for tests that are sending empty strings for no previous releases
    val versionToUse = if (version.isEmpty()) {
      "$yyyy"
    } else {
      version
    }

    return Version.from(versionToUse).next(isPublic).displayText
  }
}
