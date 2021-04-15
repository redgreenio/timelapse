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
  private val versionComponents = "${previousVersion}..".split(".")
  private val year = versionComponents[0]
  private val publishedArtifactCount = versionComponents[1]
  private val buildNumber = versionComponents[2]

  fun getYear(
    year: String
  ): String {
    return if (previousVersion.isEmpty()) {
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

  fun nextBuildNumber(
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
}

private fun displayVersion(
  previousVersion: String,
  yyyy: Int,
  isNextReleasePublic: Boolean
): String {
  val version = Version(previousVersion, yyyy, isNextReleasePublic)

  val (year, publishedArtifactCount, buildNumber) = "${previousVersion}..".split(".")

  val releaseYear = version.getYear(year)
  val nextPublishedArtifactCount = version.nextPublishedArtifactCount(isNextReleasePublic, publishedArtifactCount)
  val nextBuildNumber = version.nextBuildNumber(isNextReleasePublic, buildNumber)

  return "$releaseYear.$nextPublishedArtifactCount$nextBuildNumber"
}
