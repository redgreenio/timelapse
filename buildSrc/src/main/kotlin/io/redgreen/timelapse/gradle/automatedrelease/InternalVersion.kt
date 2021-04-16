package io.redgreen.timelapse.gradle.automatedrelease

class InternalVersion(
  yyyy: Int,
  publishedArtifactCount: Int,
  buildNumber: Int
) : Version(yyyy, publishedArtifactCount, buildNumber) {
  override val displayText: String
    get() = "$yyyy.$publishedArtifactCount.$buildNumber"

  override fun internal(): InternalVersion {
    return InternalVersion(yyyy, publishedArtifactCount, buildNumber + 1)
  }

  override fun public(): ReleaseVersion {
    return ReleaseVersion(yyyy, publishedArtifactCount + 1)
  }
}
