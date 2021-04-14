package io.redgreen.timelapse.gradle.automatedrelease

object Versioning {
  fun getNextVersion(
    yyyy: Int,
    predecessorVersion: String,
    isNextReleasePublic: Boolean = true
  ): String {
    val isPredecessorVersionInternal = predecessorVersion.split(".").size == 3
    val isNextReleaseInternal = !isNextReleasePublic
    return when {
      isNextReleaseInternal && isPredecessorVersionInternal -> {
        val (year, publishedArtifactCount, buildNumber) = predecessorVersion.split(".")
        "$year.$publishedArtifactCount.${buildNumber.toInt() + 1}"
      }
      isNextReleaseInternal && predecessorVersion.isNotBlank() -> {
        "$predecessorVersion.1"
      }
      isNextReleasePublic && predecessorVersion.isNotBlank() -> {
        val (year, publishedArtifactCount) = predecessorVersion.split(".")
        "$year.${publishedArtifactCount.toInt() + 1}"
      }
      else -> {
        if (isNextReleasePublic) {
          "$yyyy.1"
        } else {
          "2021.0.1"
        }
      }
    }
  }
}
