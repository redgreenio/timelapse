package io.redgreen.timelapse.gradle.automatedrelease

class ReleaseVersion(
  yyyy: Int,
  publishedArtifactCount: Int
) : Version(yyyy, publishedArtifactCount, UNSPECIFIED) {
  override val displayText: String
    get() = "$yyyy.$publishedArtifactCount"

  override fun next(isPublic: Boolean): Version {
    return if (isPublic) {
      ReleaseVersion(yyyy, publishedArtifactCount + 1)
    } else {
      InternalVersion(yyyy, publishedArtifactCount, 1)
    }
  }
}
