package io.redgreen.timelapse.gradle.automatedrelease

abstract class Version(
  protected val yyyy: Int,
  protected val publishedArtifactCount: Int,
  protected val buildNumber: Int
) {
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
        NoPreviousVersion(yyyy, publishedArtifactCount, buildNumber)
      } else if (buildNumberString.isEmpty()) {
        ReleaseVersion(yyyy, publishedArtifactCount, buildNumber)
      } else {
        InternalVersion(yyyy, publishedArtifactCount, buildNumber)
      }
    }
  }

  abstract val displayText: String

  fun next(isPublic: Boolean): Version {
    if (this is NoPreviousVersion) {
      return if (isPublic) {
        ReleaseVersion(yyyy, 1, -1)
      } else {
        InternalVersion(yyyy, 0, 1)
      }
    }

    if (this is ReleaseVersion) {
      return if (isPublic) {
        ReleaseVersion(yyyy, publishedArtifactCount + 1)
      } else {
        InternalVersion(yyyy, publishedArtifactCount, 1)
      }
    }

    return if (this is InternalVersion && isPublic) {
      ReleaseVersion(yyyy, publishedArtifactCount + 1)
    } else {
      InternalVersion(yyyy, publishedArtifactCount, buildNumber + 1)
    }
  }
}
