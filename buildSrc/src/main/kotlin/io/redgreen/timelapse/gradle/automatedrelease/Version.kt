package io.redgreen.timelapse.gradle.automatedrelease

open class Version(
  val displayText: String,
  private val yyyy: Int
) {
  private val versionComponents = "${displayText}..".split(".")
  private val year = versionComponents[0]
  private val publishedArtifactCount = versionComponents[1]
  private val buildNumber = versionComponents[2]

  fun nextVersion(isNextReleasePublic: Boolean): Version {
    val releaseYear = getYear()
    val nextPublishedArtifactCount = nextPublishedArtifactCount(isNextReleasePublic)
    val nextBuildNumber = nextBuildNumber(isNextReleasePublic)
    return Version("$releaseYear.$nextPublishedArtifactCount$nextBuildNumber", releaseYear.toInt())
  }

  private fun getYear(): String {
    return if (displayText.isEmpty()) {
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
