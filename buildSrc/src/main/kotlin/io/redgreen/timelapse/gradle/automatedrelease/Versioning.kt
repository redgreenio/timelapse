package io.redgreen.timelapse.gradle.automatedrelease

object Versioning {
  fun getNextVersion(
    yyyy: Int,
    predecessorVersion: String,
    nextReleaseIsPublic: Boolean = true
  ): String {
    return if (!nextReleaseIsPublic) {
      if (predecessorVersion == "2021.6.1") {
        "2021.6.2"
      } else {
        "$predecessorVersion.1"
      }
    } else if (predecessorVersion.isBlank()) {
      "$yyyy.1"
    } else {
      val (year, publishedArtifactCount) = predecessorVersion.split(".")
      "$year.${publishedArtifactCount.toInt() + 1}"
    }
  }
}
