package io.redgreen.timelapse.gradle.automatedrelease

open class Version(
  val displayText: String,
  protected val yyyy: Int
) {
  companion object {
    fun from(version: String, yyyy: Int): Version {
      val isPublicRelease = version.filter { it == '.' }.toList().count() == 2

      return if (isPublicRelease) {
        ReleaseVersion(version)
      } else if (!isPublicRelease && version.isNotEmpty()) {
        InternalVersion(version)
      } else {
        NoPreviousVersion(yyyy)
      }
    }
  }

  private val versionComponents = "${displayText}..".split(".")
  private val year = versionComponents[0]
  protected val publishedArtifactCount = versionComponents[1]
  private val buildNumber = versionComponents[2]

  fun next(isPublic: Boolean): Version {
    val releaseYear = getYear()
    val nextPublishedArtifactCount = nextPublishedArtifactCount(isPublic)
    val nextBuildNumber = nextBuildNumber(isPublic)

    return if (isPublic) {
      ReleaseVersion("$releaseYear.$nextPublishedArtifactCount$nextBuildNumber")
    } else if (!isPublic) {
      InternalVersion("$releaseYear.$nextPublishedArtifactCount$nextBuildNumber")
    } else {
      Version("$releaseYear.$nextPublishedArtifactCount$nextBuildNumber", releaseYear.toInt())
    }
  }

  protected open fun getYear(): String {
    return year
  }

  protected open fun nextPublishedArtifactCount(isPublic: Boolean): Int {
    return if (isPublic) {
      publishedArtifactCount.toInt() + 1
    } else {
      publishedArtifactCount.toInt()
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
