package io.redgreen.timelapse.gradle.automatedrelease

object Versioning {
  fun getNextVersion(yyyy: Int, predecessorVersion: String): String {
    return if (predecessorVersion.isBlank()) {
      "$yyyy.1"
    } else {
      "2021.2"
    }
  }
}
