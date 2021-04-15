package io.redgreen.timelapse.gradle.automatedrelease

class ReleaseVersion(displayText: String) : Version(displayText, -1) {
  override fun nextPublishedArtifactCount(isPublic: Boolean): Int {
    return if (isPublic) {
      publishedArtifactCount.toInt() + 1
    } else {
      publishedArtifactCount.toInt()
    }
  }
}
