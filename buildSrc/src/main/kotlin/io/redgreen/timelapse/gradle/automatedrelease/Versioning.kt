package io.redgreen.timelapse.gradle.automatedrelease

object Versioning {
  fun getNextVersion(
    yyyy: Int,
    version: String,
    isNextReleasePublic: Boolean = true
  ): String {
    return Version.from(version, yyyy).next(isNextReleasePublic).displayText
  }
}
