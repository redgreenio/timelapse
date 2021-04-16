package io.redgreen.timelapse.gradle.automatedrelease

import kotlin.LazyThreadSafetyMode.NONE

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
      val publishedArtifactCountString = versionComponents[1]
      val publishedArtifactCount = if (publishedArtifactCountString.isEmpty()) 0 else publishedArtifactCountString.toInt()
      val buildNumberString = versionComponents[2]
      val buildNumber = if (buildNumberString.isEmpty()) 0 else buildNumberString.toInt()

      return if (publishedArtifactCountString.isEmpty() && buildNumberString.isEmpty()) {
        NoPreviousVersion(yyyy, publishedArtifactCount, buildNumber)
      } else if (isPublicRelease) {
        ReleaseVersion(displayText, yyyy, publishedArtifactCount, buildNumber)
      } else {
        InternalVersion(displayText, yyyy, publishedArtifactCount, buildNumber)
      }
    }
  }

  open val neoDisplayText: String by lazy(NONE) { displayText }

  fun next(isPublic: Boolean): Version {
    val nextPublishedArtifactCount = nextPublishedArtifactCount(isPublic)
    val nextBuildNumber = nextBuildNumber(isPublic)

    return from("${yyyy}.$nextPublishedArtifactCount$nextBuildNumber", yyyy)
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
