package io.redgreen.timelapse.gradle.automatedrelease

abstract class Version(
  val displayText: String,
  private val yyyy: Int,
  private val publishedArtifactCount: Int
) {
  companion object {
    fun from(displayText: String, yyyy: Int): Version {
      val isPublicRelease = displayText.filter { it == '.' }.toList().count() == 2

      // TODO Get rid of this duplication
      val versionComponents = "${displayText}..".split(".")
      val publishedArtifactCount: Int = if (versionComponents[1].isEmpty()) 0 else versionComponents[1].toInt()

      return if (displayText.isEmpty()) {
        NoPreviousVersion(yyyy, publishedArtifactCount)
      } else if (isPublicRelease) {
        ReleaseVersion(displayText, yyyy, publishedArtifactCount)
      } else {
        InternalVersion(displayText, yyyy, publishedArtifactCount)
      }
    }
  }

  private val versionComponents = "${displayText}..".split(".")
  private val buildNumber = versionComponents[2]

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
    } else if (buildNumber.isEmpty()) {
      ".1"
    } else {
      ".${buildNumber.toInt() + 1}"
    }
  }
}
