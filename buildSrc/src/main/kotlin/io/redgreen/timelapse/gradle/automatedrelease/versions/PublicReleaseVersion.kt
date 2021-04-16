package io.redgreen.timelapse.gradle.automatedrelease.versions

import io.redgreen.timelapse.gradle.automatedrelease.Version

class PublicReleaseVersion(
  private val yyyy: Int,
  private val publishedArtifactCount: Int
) : Version {
  override val displayText: String
    get() = "$yyyy.$publishedArtifactCount"

  override fun internal(): InternalVersion {
    return InternalVersion(yyyy, publishedArtifactCount, 1)
  }

  override fun public(): PublicReleaseVersion {
    return PublicReleaseVersion(yyyy, publishedArtifactCount + 1)
  }
}
