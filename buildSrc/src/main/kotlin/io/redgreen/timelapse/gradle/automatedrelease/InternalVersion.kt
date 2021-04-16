package io.redgreen.timelapse.gradle.automatedrelease

class InternalVersion(
  yyyy: Int,
  publishedArtifactCount: Int,
  buildNumber: Int
) : Version(yyyy, publishedArtifactCount, buildNumber) {
  override val displayText: String
    get() = "$yyyy.$publishedArtifactCount.$buildNumber"

  override fun next(isPublic: Boolean): Version {
    return if (isPublic) {
      ReleaseVersion(yyyy, publishedArtifactCount + 1)
    } else {
      InternalVersion(yyyy, publishedArtifactCount, buildNumber + 1)
    }
  }
}
