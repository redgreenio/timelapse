package io.redgreen.timelapse.gradle.automatedrelease

open class Version(
  val displayText: String,
  protected val yyyy: Int
) {
  companion object {
    fun from(displayText: String, yyyy: Int): Version {
      val isPublicRelease = displayText.filter { it == '.' }.toList().count() == 2

      return if (displayText.isEmpty()) {
        NoPreviousVersion(yyyy)
      } else if (isPublicRelease) {
        ReleaseVersion(displayText)
      } else {
        InternalVersion(displayText)
      }
    }
  }

  private val versionComponents = "${displayText}..".split(".")
  private val year = versionComponents[0]
  private val publishedArtifactCount: Int = if (versionComponents[1].isEmpty()) 0 else versionComponents[1].toInt()
  private val buildNumber = versionComponents[2]

  fun next(isPublic: Boolean): Version {
    val nextPublishedArtifactCount = nextPublishedArtifactCount(isPublic)
    val nextBuildNumber = nextBuildNumber(isPublic)

    return from("${getYear()}.$nextPublishedArtifactCount$nextBuildNumber", -1)
  }

  protected open fun getYear(): String {
    return year
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
