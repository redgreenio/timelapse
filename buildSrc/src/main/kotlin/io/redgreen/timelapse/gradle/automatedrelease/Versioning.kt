package io.redgreen.timelapse.gradle.automatedrelease

object Versioning {
  fun getNextVersion(
    yyyy: Int,
    predecessorVersion: String,
    isNextReleasePublic: Boolean = true
  ): String {
    val isPredecessorVersionInternal = predecessorVersion.split(".").size == 3

    val (year, publishedArtifactCount, buildNumber) = "${predecessorVersion}..".split(".")

    val internalToInternal = isPredecessorVersionInternal && !isNextReleasePublic
    val internalOrPublicToPublic = predecessorVersion.isNotBlank() && isNextReleasePublic
    val internalOrPublicToInternal = !isNextReleasePublic && predecessorVersion.isNotBlank()

    return if (internalToInternal || internalOrPublicToPublic || internalOrPublicToInternal) {
      "$year.${getPublishedArtifactCount(isNextReleasePublic, publishedArtifactCount)}${getBuildNumber(isNextReleasePublic, buildNumber)}"
    } else if (isNextReleasePublic) {
      "$yyyy.1"
    } else {
      "$yyyy.0.1"
    }
  }

  private fun getPublishedArtifactCount(
    isNextReleasePublic: Boolean,
    publishedArtifactCount: String
  ): Int {
    return if (isNextReleasePublic) {
      publishedArtifactCount.toInt() + 1
    } else {
      publishedArtifactCount.toInt()
    }
  }

  private fun getBuildNumber(
    isNextReleasePublic: Boolean,
    buildNumber: String
  ): String {
    return if (isNextReleasePublic) {
      ""
    } else if (buildNumber.isEmpty()) {
      ".1"
    } else {
      ".${buildNumber.toInt() + 1}"
    }
  }
}
