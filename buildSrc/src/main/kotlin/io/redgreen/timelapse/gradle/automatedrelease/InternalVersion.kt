package io.redgreen.timelapse.gradle.automatedrelease

class InternalVersion(
  displayText: String,
  publishedArtifactCount: Int
) : Version(displayText, -1, publishedArtifactCount)
