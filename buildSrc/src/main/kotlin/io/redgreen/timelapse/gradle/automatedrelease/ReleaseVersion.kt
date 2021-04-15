package io.redgreen.timelapse.gradle.automatedrelease

class ReleaseVersion(displayText: String) : Version(displayText, -1) {
  override fun nextPublishedArtifactCount(isPublic: Boolean): Int {
    return if (publishedArtifactCount.isEmpty() && isPublic) {
      TODO()
    } else if (isPublic) {
      publishedArtifactCount.toInt() + 1
    } else {
      publishedArtifactCount.toInt()
    }
  }

  override fun nextBuildNumber(isPublic: Boolean): String {
    return if (isPublic) {
      ""
    } else if (buildNumber.isEmpty()) {
      ".1"
    } else {
      ".${buildNumber.toInt() + 1}"
    }
  }
}
