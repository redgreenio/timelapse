package io.redgreen.timelapse.gradle.automatedrelease

class ReleaseVersion(override val displayText: String) : Version(displayText, -1) {
  override fun nextPublishedArtifactCount(isNextReleasePublic: Boolean): Int {
    return if (publishedArtifactCount.isEmpty()) {
      1
    } else {
      publishedArtifactCount.toInt() + 1
    }
  }

  override fun nextBuildNumber(isNextReleasePublic: Boolean): String {
    return if (isNextReleasePublic) {
      ""
    } else if (buildNumber.isEmpty()) {
      ".1"
    } else {
      ".${buildNumber.toInt() + 1}"
    }
  }
}
