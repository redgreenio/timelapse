package io.redgreen.timelapse.gradle.automatedrelease

object Versioning {
  fun getNextVersion(yyyy: Int, predecessorVersion: String): String {
    return if (predecessorVersion.isBlank()) {
      "$yyyy.1"
    } else {
      val (year, publishedArtifactCount) = predecessorVersion.split(".")
      return "$year.${publishedArtifactCount.toInt() + 1}"
    }
  }
}
