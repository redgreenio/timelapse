package io.redgreen.timelapse.gradle.automatedrelease

open class Version(
  open val displayText: String,
  private val yyyy: Int
) {
  private val versionComponents = "${displayText}..".split(".")
  private val year = versionComponents[0]
  protected val publishedArtifactCount = versionComponents[1]
  protected val buildNumber = versionComponents[2]

  fun nextVersion(isNextReleasePublic: Boolean): Version {
    val releaseYear = getYear()
    val nextPublishedArtifactCount = nextPublishedArtifactCount(isNextReleasePublic)
    val nextBuildNumber = nextBuildNumber(isNextReleasePublic)
    return if (isNextReleasePublic) {
      ReleaseVersion("$releaseYear.$nextPublishedArtifactCount$nextBuildNumber")
    } else {
      Version("$releaseYear.$nextPublishedArtifactCount$nextBuildNumber", releaseYear.toInt())
    }
  }

  private fun getYear(): String {
    return if (displayText.isEmpty()) {
      "$yyyy"
    } else {
      year
    }
  }

  protected open fun nextPublishedArtifactCount(isNextReleasePublic: Boolean): Int {
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

  protected open fun nextBuildNumber(isNextReleasePublic: Boolean): String {
    return if (isNextReleasePublic) {
      ""
    } else if (buildNumber.isEmpty()) {
      ".1"
    } else {
      ".${buildNumber.toInt() + 1}"
    }
  }
}
