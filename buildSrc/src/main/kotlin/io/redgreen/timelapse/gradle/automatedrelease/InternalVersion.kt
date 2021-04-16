package io.redgreen.timelapse.gradle.automatedrelease

class InternalVersion(
  displayText: String,
  yyyy: Int,
  publishedArtifactCount: Int,
  buildNumber: Int = NO_BUILDS
) : Version(displayText, yyyy, publishedArtifactCount, buildNumber) {
  companion object {
    private const val NO_BUILDS = 0
  }
}
