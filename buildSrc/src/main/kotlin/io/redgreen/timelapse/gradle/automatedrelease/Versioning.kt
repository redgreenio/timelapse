package io.redgreen.timelapse.gradle.automatedrelease

object Versioning {
  fun getNextVersion(
    yyyy: Int,
    version: String,
    isPublic: Boolean = true
  ): String {
    return Version.from(version, yyyy).next(isPublic).displayText
  }
}
