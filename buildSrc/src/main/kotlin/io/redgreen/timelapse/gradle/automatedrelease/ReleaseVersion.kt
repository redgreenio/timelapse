package io.redgreen.timelapse.gradle.automatedrelease

class ReleaseVersion(
  private val yyyy: Int,
  private val publishedArtifactCount: Int
) : Version() {
  override val displayText: String
    get() = "$yyyy.$publishedArtifactCount"

  override fun internal(): InternalVersion {
    return InternalVersion(yyyy, publishedArtifactCount, 1)
  }

  override fun public(): ReleaseVersion {
    return ReleaseVersion(yyyy, publishedArtifactCount + 1)
  }
}
