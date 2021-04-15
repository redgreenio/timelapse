package io.redgreen.timelapse.gradle.automatedrelease

class ReleaseVersion(displayText: String) : Version(displayText, -1) {
  override fun nextPublishedArtifactCount(isNextReleasePublic: Boolean): Int {
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
