package io.redgreen.timelapse.gradle.automatedrelease

object Versioning {
  fun getNextVersion(
    yyyy: Int,
    previousVersion: String,
    isNextReleasePublic: Boolean = true
  ): String {
    return Version(previousVersion, yyyy).nextVersionText(isNextReleasePublic)
  }
}

class Version(
  private val versionText: String,
  private val yyyy: Int
) {
  private val versionComponents = "${versionText}..".split(".")
  private val year = versionComponents[0]
  private val publishedArtifactCount = versionComponents[1]
  private val buildNumber = versionComponents[2]

  fun nextVersionText(isNextReleasePublic: Boolean): String {
    val releaseYear = getYear()
    val nextPublishedArtifactCount = nextPublishedArtifactCount(isNextReleasePublic)
    val nextBuildNumber = nextBuildNumber(isNextReleasePublic)

    return "$releaseYear.$nextPublishedArtifactCount$nextBuildNumber"
  }

  private fun getYear(): String {
    return if (versionText.isEmpty()) {
      "$yyyy"
    } else {
      year
    }
  }

  private fun nextPublishedArtifactCount(isNextReleasePublic: Boolean): Int {
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

  private fun nextBuildNumber(isNextReleasePublic: Boolean): String {
    return if (isNextReleasePublic) {
      ""
    } else if (buildNumber.isEmpty()) {
      ".1"
    } else {
      ".${buildNumber.toInt() + 1}"
    }
  }
}
