package io.redgreen.timelapse.gradle.automatedrelease

class ReleaseVersion(
  yyyy: Int,
  publishedArtifactCount: Int,
  buildNumber: Int = 0
) : Version(yyyy, publishedArtifactCount, buildNumber) {
  override val displayText: String
    get() = "$yyyy.$publishedArtifactCount"
}
