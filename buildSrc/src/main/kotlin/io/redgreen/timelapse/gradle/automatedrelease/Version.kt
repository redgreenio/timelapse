package io.redgreen.timelapse.gradle.automatedrelease

abstract class Version(
  val displayText: String,
  private val yyyy: Int,
  private val publishedArtifactCount: Int,
  private val buildNumber: Int
) {
  companion object {
    fun from(displayText: String, yyyy: Int): Version {
      val isPublicRelease = displayText.filter { it == '.' }.toList().count() == 2

      // TODO Get rid of this duplication
      val versionComponents = "${displayText}..".split(".")
      val publishedArtifactCount = if (versionComponents[1].isEmpty()) 0 else versionComponents[1].toInt()
      val buildNumber = if (versionComponents[2].isEmpty()) 0 else versionComponents[2].toInt()

      return if (displayText.isEmpty()) {
        NoPreviousVersion(yyyy, publishedArtifactCount, buildNumber)
      } else if (isPublicRelease) {
        ReleaseVersion(displayText, yyyy, publishedArtifactCount, buildNumber)
      } else {
        InternalVersion(displayText, yyyy, publishedArtifactCount, buildNumber)
      }
    }
  }

  fun next(isPublic: Boolean): Version {
    val nextPublishedArtifactCount = nextPublishedArtifactCount(isPublic)
    val nextBuildNumber = nextBuildNumber(isPublic)

    return from("${yyyy}.$nextPublishedArtifactCount$nextBuildNumber", -1)
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
