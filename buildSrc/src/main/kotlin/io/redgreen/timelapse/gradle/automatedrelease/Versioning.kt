package io.redgreen.timelapse.gradle.automatedrelease

object Versioning {
  fun getNextVersion(
    yyyy: Int,
    predecessorVersion: String,
    isNextReleasePublic: Boolean = true
  ): String {
    val isPredecessorVersionInternal = predecessorVersion.split(".").size == 3
    val isNextReleaseInternal = !isNextReleasePublic

    val (year, publishedArtifactCount, buildNumber) = "${predecessorVersion}..".split(".")

    return if (isNextReleaseInternal && isPredecessorVersionInternal) {
      "$year.$publishedArtifactCount.${buildNumber.toInt() + 1}"
    } else if (isNextReleaseInternal && predecessorVersion.isNotBlank()) {
      "$predecessorVersion.1"
    } else if (isNextReleasePublic && predecessorVersion.isNotBlank()) {
      "$year.${getPublishedArtifactCount(publishedArtifactCount)}"
    } else if (isNextReleasePublic) {
      "$yyyy.1"
    } else {
      "$yyyy.0.1"
    }
  }

  private fun getPublishedArtifactCount(publishedArtifactCount: String): Int {
    return publishedArtifactCount.toInt() + 1
  }
}
