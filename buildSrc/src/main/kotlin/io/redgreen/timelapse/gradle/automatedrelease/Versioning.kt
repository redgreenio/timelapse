package io.redgreen.timelapse.gradle.automatedrelease

object Versioning {
  fun getNextVersion(
    yyyy: Int,
    previousVersion: String,
    isNextReleasePublic: Boolean = true
  ): String {
    return Version(previousVersion, yyyy).nextVersion(isNextReleasePublic).displayText
  }
}
