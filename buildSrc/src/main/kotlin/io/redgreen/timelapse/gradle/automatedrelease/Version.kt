package io.redgreen.timelapse.gradle.automatedrelease

abstract class Version(
  protected val yyyy: Int,
  protected val publishedArtifactCount: Int,
  protected val buildNumber: Int
) : NextVersion {
  companion object {
    internal const val UNSPECIFIED = -1

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

  abstract val displayText: String

  abstract fun next(isPublic: Boolean): Version

  override fun internal(): InternalVersion {
    TODO("Not yet implemented")
  }

  override fun public(): ReleaseVersion {
    TODO("Not yet implemented")
  }
}
