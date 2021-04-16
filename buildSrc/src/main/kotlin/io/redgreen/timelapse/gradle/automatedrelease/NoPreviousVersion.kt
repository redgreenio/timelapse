package io.redgreen.timelapse.gradle.automatedrelease

class NoPreviousVersion(
  yyyy: Int,
  publishedArtifactCount: Int,
  buildNumber: Int = 0
) : Version("", yyyy, publishedArtifactCount, buildNumber) {
  override fun nextPublishedArtifactCount(isPublic: Boolean): Int {
    return if (isPublic) {
      1
    } else {
      0
    }
  }
}