package io.redgreen.timelapse.gradle.automatedrelease

import io.redgreen.timelapse.gradle.automatedrelease.versions.NoPreviousVersion

interface Version : NextVersion {
  companion object {
    // Parsing logic -> Git tags to version objects
    fun from(displayText: String): Version {
      val versionComponents = "${displayText}..".split(".")
      val yyyy = versionComponents[0].toInt()
      val publishedArtifactCountString = versionComponents[1]
      val publishedArtifactCount = if (publishedArtifactCountString.isEmpty()) 0 else publishedArtifactCountString.toInt()
      val buildNumberString = versionComponents[2]
      val buildNumber = if (buildNumberString.isEmpty()) 0 else buildNumberString.toInt()

      return if (publishedArtifactCountString.isEmpty() && buildNumberString.isEmpty()) {
        NoPreviousVersion(yyyy)
      } else if (buildNumberString.isEmpty()) {
        ReleaseVersion(yyyy, publishedArtifactCount)
      } else {
        InternalVersion(yyyy, publishedArtifactCount, buildNumber)
      }
    }
  }

  val displayText: String
}
