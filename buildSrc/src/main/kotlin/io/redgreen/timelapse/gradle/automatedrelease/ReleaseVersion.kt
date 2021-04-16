package io.redgreen.timelapse.gradle.automatedrelease

class ReleaseVersion(
  displayText: String,
  yyyy: Int,
  publishedArtifactCount: Int,
  buildNumber: Int = 0
) : Version(yyyy, publishedArtifactCount, buildNumber) {
  override val neoDisplayText: String
    get() = "$yyyy.$publishedArtifactCount"
}
