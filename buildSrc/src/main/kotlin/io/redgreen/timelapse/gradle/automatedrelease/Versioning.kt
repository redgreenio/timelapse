package io.redgreen.timelapse.gradle.automatedrelease

object Versioning {
  fun getNextVersion(
    yyyy: Int,
    predecessorVersion: String,
    isNextReleasePublic: Boolean = true
  ): String {
    val isPredecessorVersionInternal = predecessorVersion.split(".").size == 3

    val (year, publishedArtifactCount, buildNumber) = "${predecessorVersion}..".split(".")

    return if (!isNextReleasePublic && isPredecessorVersionInternal) {
      "$year.${getPublishedArtifactCount(publishedArtifactCount, isNextReleasePublic)}.${buildNumber.toInt() + 1}"
    } else if (!isNextReleasePublic && predecessorVersion.isNotBlank()) {
      "$predecessorVersion.1"
    } else if (isNextReleasePublic && predecessorVersion.isNotBlank()) {
      "$year.${getPublishedArtifactCount(publishedArtifactCount, isNextReleasePublic)}"
    } else if (isNextReleasePublic) {
      "$yyyy.1"
    } else {
      "$yyyy.0.1"
    }
  }

  private fun getPublishedArtifactCount(
    publishedArtifactCount: String,
    isNextReleasePublic: Boolean
  ): Int {
    return if (isNextReleasePublic) {
      publishedArtifactCount.toInt() + 1
    } else {
      publishedArtifactCount.toInt()
    }
  }
}
