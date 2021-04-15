package io.redgreen.timelapse.gradle.automatedrelease

object Versioning {
  fun getNextVersion(
    yyyy: Int,
    previousVersion: String,
    isNextReleasePublic: Boolean = true
  ): String {
    val (year, publishedArtifactCount, buildNumber) = "${previousVersion}..".split(".")

    return displayVersion(previousVersion, yyyy, isNextReleasePublic, year, publishedArtifactCount, buildNumber)
  }

  private fun displayVersion(
    previousVersion: String,
    yyyy: Int,
    isNextReleasePublic: Boolean,
    year: String,
    publishedArtifactCount: String,
    buildNumber: String
  ): String {
    val releaseYear = getYear(previousVersion, yyyy, year)
    val nextPublishedArtifactCount = nextPublishedArtifactCount(isNextReleasePublic, publishedArtifactCount)
    val nextBuildNumber = nextBuildNumber(isNextReleasePublic, buildNumber)

    return "$releaseYear.$nextPublishedArtifactCount$nextBuildNumber"
  }
}

private fun getYear(
  predecessorVersion: String,
  yyyy: Int,
  year: String
): String {
  return if (predecessorVersion.isEmpty()) {
    "$yyyy"
  } else {
    year
  }
}

private fun nextPublishedArtifactCount(
  isNextReleasePublic: Boolean,
  previousPublishedArtifactCount: String
): Int {
  return if (previousPublishedArtifactCount.isEmpty() && isNextReleasePublic) {
    1
  } else if (isNextReleasePublic) {
    previousPublishedArtifactCount.toInt() + 1
  } else if (previousPublishedArtifactCount.isEmpty()) {
    0
  } else {
    previousPublishedArtifactCount.toInt()
  }
}

private fun nextBuildNumber(
  isNextReleasePublic: Boolean,
  previousBuildNumber: String
): String {
  return if (isNextReleasePublic) {
    ""
  } else if (previousBuildNumber.isEmpty()) {
    ".1"
  } else {
    ".${previousBuildNumber.toInt() + 1}"
  }
}
