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

    return Version.from(version, yearToUse).next(isPublic).displayText
  }
}
