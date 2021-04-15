package io.redgreen.timelapse.gradle.automatedrelease

object Versioning {
  fun getNextVersion(
    yyyy: Int,
    previousVersion: String,
    isNextReleasePublic: Boolean = true
  ): String {
    val version = Version.from(previousVersion, yyyy)
    val nextVersion = version.next(isNextReleasePublic)
    return nextVersion.displayText
  }
}
