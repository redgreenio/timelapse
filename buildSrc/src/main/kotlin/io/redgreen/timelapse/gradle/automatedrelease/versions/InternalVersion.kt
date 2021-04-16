package io.redgreen.timelapse.gradle.automatedrelease.versions

import io.redgreen.timelapse.gradle.automatedrelease.ReleaseVersion
import io.redgreen.timelapse.gradle.automatedrelease.Version

class InternalVersion(
  private val yyyy: Int,
  private val publishedArtifactCount: Int,
  private val buildNumber: Int
) : Version {
  override val displayText: String
    get() = "$yyyy.$publishedArtifactCount.$buildNumber"

  override fun internal(): InternalVersion {
    return InternalVersion(yyyy, publishedArtifactCount, buildNumber + 1)
  }

  override fun public(): ReleaseVersion {
    return ReleaseVersion(yyyy, publishedArtifactCount + 1)
  }
}
