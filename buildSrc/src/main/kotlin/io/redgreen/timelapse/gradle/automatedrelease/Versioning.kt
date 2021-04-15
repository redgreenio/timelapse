package io.redgreen.timelapse.gradle.automatedrelease

object Versioning {
  fun getNextVersion(
    yyyy: Int,
    predecessorVersion: String,
    isNextReleasePublic: Boolean = true
  ): String {
    val (year, publishedArtifactCount, buildNumber) = "${predecessorVersion}..".split(".")
    val yearToUse = if (predecessorVersion.isEmpty()) {
      "$yyyy"
    } else {
      year
    }

    return "$yearToUse.${getPublishedArtifactCount(isNextReleasePublic, publishedArtifactCount)}${getBuildNumber(isNextReleasePublic, buildNumber)}"
  }

  private fun getPublishedArtifactCount(
    isNextReleasePublic: Boolean,
    publishedArtifactCount: String
  ): Int {
    return if (publishedArtifactCount.isEmpty() && isNextReleasePublic) {
      1
    } else if (isNextReleasePublic) {
      publishedArtifactCount.toInt() + 1
    } else if (publishedArtifactCount.isEmpty()) {
      0
    } else {
      publishedArtifactCount.toInt()
    }
  }

  private fun getBuildNumber(
    isNextReleasePublic: Boolean,
    buildNumber: String
  ): String {
    return if (isNextReleasePublic) {
      ""
    } else if (buildNumber.isEmpty()) {
      ".1"
    } else {
      ".${buildNumber.toInt() + 1}"
    }
  }
}
