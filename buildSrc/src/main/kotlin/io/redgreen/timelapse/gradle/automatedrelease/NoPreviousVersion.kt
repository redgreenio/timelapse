package io.redgreen.timelapse.gradle.automatedrelease

class NoPreviousVersion(
  yyyy: Int,
  publishedArtifactCount: Int,
  buildNumber: Int
) : Version(yyyy, publishedArtifactCount, buildNumber) {
  override val neoDisplayText: String
    get() = "$yyyy"
}
