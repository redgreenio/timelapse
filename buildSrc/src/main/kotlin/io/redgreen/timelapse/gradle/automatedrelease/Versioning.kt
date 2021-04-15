package io.redgreen.timelapse.gradle.automatedrelease

object Versioning {
  fun getNextVersion(
    yyyy: Int,
    previousVersion: String,
    isNextReleasePublic: Boolean = true
  ): String {
    return displayVersion(previousVersion, yyyy, isNextReleasePublic)
  }
}

class Version(
  private val previousVersion: String,
  private val yyyy: Int,
  private val isNextReleasePublic: Boolean
) {
  fun getYear(
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

  fun nextPublishedArtifactCount(
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
}

private fun displayVersion(
  previousVersion: String,
  yyyy: Int,
  isNextReleasePublic: Boolean
): String {
  val version = Version(previousVersion, yyyy, isNextReleasePublic)

  val (year, publishedArtifactCount, buildNumber) = "${previousVersion}..".split(".")

  val releaseYear = version.getYear(previousVersion, yyyy, year)
  val nextPublishedArtifactCount = version.nextPublishedArtifactCount(isNextReleasePublic, publishedArtifactCount)
  val nextBuildNumber = nextBuildNumber(isNextReleasePublic, buildNumber)

  return "$releaseYear.$nextPublishedArtifactCount$nextBuildNumber"
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
