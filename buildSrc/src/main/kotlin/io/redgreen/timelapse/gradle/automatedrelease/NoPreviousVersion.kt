package io.redgreen.timelapse.gradle.automatedrelease

class NoPreviousVersion(
  yyyy: Int,
  publishedArtifactCount: Int,
  buildNumber: Int
) : Version(yyyy, publishedArtifactCount, buildNumber) {
  override val displayText: String
    get() = "$yyyy"

  override fun next(isPublic: Boolean): Version {
    return if (isPublic) {
      ReleaseVersion(yyyy, 1, -1)
    } else {
      InternalVersion(yyyy, 0, 1)
    }
  }
}
