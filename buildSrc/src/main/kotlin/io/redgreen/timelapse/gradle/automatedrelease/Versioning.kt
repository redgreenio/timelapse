package io.redgreen.timelapse.gradle.automatedrelease

object Versioning {
  fun getReleaseVersion(yyyy: Int): String {
    return "$yyyy.1"
  }
}
