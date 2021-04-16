package io.redgreen.timelapse.gradle.automatedrelease

class InternalVersion(
  yyyy: Int,
  publishedArtifactCount: Int,
  buildNumber: Int
) : Version(yyyy, publishedArtifactCount, buildNumber) {
  override val displayText: String
    get() = "$yyyy.$publishedArtifactCount.$buildNumber"
}
