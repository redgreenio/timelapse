package io.redgreen.timelapse.gradle.automatedrelease

class InternalVersion(
  displayText: String,
  yyyy: Int,
  publishedArtifactCount: Int,
  buildNumber: Int
) : Version(yyyy, publishedArtifactCount, buildNumber) {
  override val neoDisplayText: String
    get() = "$yyyy.$publishedArtifactCount.$buildNumber"
}
