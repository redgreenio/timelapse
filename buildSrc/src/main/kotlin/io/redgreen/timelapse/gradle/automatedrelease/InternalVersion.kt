package io.redgreen.timelapse.gradle.automatedrelease

class InternalVersion(
  displayText: String,
  yyyy: Int,
  publishedArtifactCount: Int,
  buildNumber: Int
) : Version(displayText, yyyy, publishedArtifactCount, buildNumber)
