package io.redgreen.timelapse.gradle.automatedrelease

abstract class Version(
  protected val yyyy: Int,
  protected val publishedArtifactCount: Int,
  protected val buildNumber: Int
) {
  companion object {
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
        ReleaseVersion(displayText, yyyy, publishedArtifactCount, buildNumber)
      } else {
        InternalVersion(displayText, yyyy, publishedArtifactCount, buildNumber)
      }
    }
  }

  abstract val displayText: String

  fun next(isPublic: Boolean): Version {
    val nextPublishedArtifactCount = nextPublishedArtifactCount(isPublic)
    val nextBuildNumber = nextBuildNumber(isPublic)

    return from("${yyyy}.$nextPublishedArtifactCount$nextBuildNumber")
  }

  protected open fun nextPublishedArtifactCount(isPublic: Boolean): Int {
    return if (isPublic) {
      publishedArtifactCount + 1
    } else {
      publishedArtifactCount
    }
  }

  protected open fun nextBuildNumber(isPublic: Boolean): String {
    return if (isPublic) {
      ""
    } else if (buildNumber == 0) {
      ".1"
    } else {
      ".${buildNumber + 1}"
    }
  }
}
