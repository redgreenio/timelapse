package io.redgreen.timelapse.gradle.automatedrelease

object Versioning {
  fun getNextVersion(
    yyyy: Int,
    predecessorVersion: String,
    nextReleaseIsPublic: Boolean = true
  ): String {
    val isPredecessorVersionInternal = predecessorVersion.split(".").size == 3
    return if (!nextReleaseIsPublic && isPredecessorVersionInternal) {
      val (year, publishedArtifactCount, buildNumber) = predecessorVersion.split(".")
      "$year.$publishedArtifactCount.${buildNumber.toInt() + 1}"
    } else if (!nextReleaseIsPublic) {
      "$predecessorVersion.1"
    } else if (predecessorVersion.isBlank()) {
      "$yyyy.1"
    } else {
      val (year, publishedArtifactCount) = predecessorVersion.split(".")
      "$year.${publishedArtifactCount.toInt() + 1}"
    }
  }
}
