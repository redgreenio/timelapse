package io.redgreen.timelapse.gradle.automatedrelease

class ReleaseVersion(
  displayText: String,
  yyyy: Int,
  publishedArtifactCount: Int
) : Version(displayText, yyyy, publishedArtifactCount)
