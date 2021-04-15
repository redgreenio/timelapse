package io.redgreen.timelapse.gradle.automatedrelease

object Versioning {
  fun getNextVersion(
    yyyy: Int,
    predecessorVersion: String,
    isNextReleasePublic: Boolean = true
  ): String {
    val (year, publishedArtifactCount, buildNumber) = "${predecessorVersion}..".split(".")

    val nextReleaseYear = getYear(predecessorVersion, yyyy, year)
    val nextPublishedArtifactCount = getPublishedArtifactCount(isNextReleasePublic, publishedArtifactCount)
    val nextBuildNumber = getBuildNumber(isNextReleasePublic, buildNumber)

    return "$nextReleaseYear.$nextPublishedArtifactCount$nextBuildNumber"
  }

  private fun getYear(predecessorVersion: String, yyyy: Int, year: String): String {
    return if (predecessorVersion.isEmpty()) {
      "$yyyy"
    } else {
      year
    }
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
