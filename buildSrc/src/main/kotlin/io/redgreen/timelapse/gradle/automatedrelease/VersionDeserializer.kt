package io.redgreen.timelapse.gradle.automatedrelease

import io.redgreen.timelapse.gradle.automatedrelease.versions.InternalVersion
import io.redgreen.timelapse.gradle.automatedrelease.versions.NoPreviousVersion
import io.redgreen.timelapse.gradle.automatedrelease.versions.PublicReleaseVersion

object VersionDeserializer {
  fun deserialize(displayText: String): Version {
    val versionComponents = "${displayText}..".split(".")
    val yyyy = versionComponents[0].toInt()
    val publishedArtifactCountString = versionComponents[1]
    val publishedArtifactCount = if (publishedArtifactCountString.isEmpty()) 0 else publishedArtifactCountString.toInt()
    val buildNumberString = versionComponents[2]
    val buildNumber = if (buildNumberString.isEmpty()) 0 else buildNumberString.toInt()

    return if (publishedArtifactCountString.isEmpty() && buildNumberString.isEmpty()) {
      NoPreviousVersion(yyyy)
    } else if (buildNumberString.isEmpty()) {
      PublicReleaseVersion(yyyy, publishedArtifactCount)
    } else {
      InternalVersion(yyyy, publishedArtifactCount, buildNumber)
    }
  }
}
