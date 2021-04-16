package io.redgreen.timelapse.gradle.automatedrelease

class NoPreviousVersion(
  yyyy: Int,
  publishedArtifactCount: Int,
  buildNumber: Int
) : Version("$yyyy", yyyy, publishedArtifactCount, buildNumber)
