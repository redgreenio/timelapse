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

  fun getYear(): String {
    return if (previousVersion.isEmpty()) {
      "$yyyy"
    } else {
      year
    }
  }

  fun nextPublishedArtifactCount(isNextReleasePublic: Boolean): Int {
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

  fun nextBuildNumber(isNextReleasePublic: Boolean): String {
    return if (isNextReleasePublic) {
      ""
    } else if (buildNumber.isEmpty()) {
      ".1"
    } else {
      ".${buildNumber.toInt() + 1}"
    }
  }
}

private fun displayVersion(
  previousVersion: String,
  yyyy: Int,
  isNextReleasePublic: Boolean
): String {
  val version = Version(previousVersion, yyyy, isNextReleasePublic)

  val releaseYear = version.getYear()
  val nextPublishedArtifactCount = version.nextPublishedArtifactCount(isNextReleasePublic)
  val nextBuildNumber = version.nextBuildNumber(isNextReleasePublic)

  return "$releaseYear.$nextPublishedArtifactCount$nextBuildNumber"
}
