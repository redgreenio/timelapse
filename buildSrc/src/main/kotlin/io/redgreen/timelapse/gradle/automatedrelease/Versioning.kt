package io.redgreen.timelapse.gradle.automatedrelease

object Versioning {
  fun getNextVersion(
    yyyy: Int,
    version: String,
    isPublic: Boolean = true
  ): String {
    val yearFromDisplayTextString = "${version}..".split(".").first()

    val yearToUse = if (yearFromDisplayTextString.isEmpty() || yyyy == yearFromDisplayTextString.toInt()) {
      yyyy
    } else {
      yearFromDisplayTextString.toInt()
    }

    // Temporary fix for tests that are sending empty strings for no previous releases
    val versionToUse = if (version.isEmpty()) {
      "$yyyy"
    } else {
      version
    }

    return Version.from(versionToUse, yearToUse).next(isPublic).neoDisplayText
  }
}
