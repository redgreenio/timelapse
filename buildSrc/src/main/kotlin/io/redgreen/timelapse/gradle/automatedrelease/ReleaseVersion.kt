package io.redgreen.timelapse.gradle.automatedrelease

class ReleaseVersion(
  displayText: String,
  publishedArtifactCount: Int
) : Version(displayText, -1, publishedArtifactCount)
