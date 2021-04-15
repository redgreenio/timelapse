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
      "$year.${getPublishedArtifactCount(publishedArtifactCount, isNextReleasePublic)}${getBuildNumber(isNextReleasePublic, buildNumber)}"
    } else if (!isNextReleasePublic && predecessorVersion.isNotBlank()) {
      "$predecessorVersion.1"
    } else if (isNextReleasePublic && predecessorVersion.isNotBlank()) {
      "$year.${getPublishedArtifactCount(publishedArtifactCount, isNextReleasePublic)}${getBuildNumber(isNextReleasePublic, buildNumber)}"
    } else if (isNextReleasePublic) {
      "$yyyy.1"
    } else {
      "$yyyy.0.1"
    }
  }

  private fun getBuildNumber(
    isNextReleasePublic: Boolean,
    buildNumber: String
  ): String {
    return if (isNextReleasePublic) {
      ""
    } else {
      ".${buildNumber.toInt() + 1}"
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
