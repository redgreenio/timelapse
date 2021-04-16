package io.redgreen.timelapse.gradle.automatedrelease

object Versioning {
  fun getNextVersion(
    yyyy: Int,
    displayText: String,
    isPublic: Boolean = true
  ): String {
    // Temporary fix for tests that are sending empty strings for no previous releases
    val versionToUse = if (displayText.isEmpty()) {
      "$yyyy"
    } else {
      displayText
    }

    val version = Version.from(versionToUse)
    return if (isPublic) {
      version.public()
    } else {
      version.internal()
    }.displayText
  }
}
