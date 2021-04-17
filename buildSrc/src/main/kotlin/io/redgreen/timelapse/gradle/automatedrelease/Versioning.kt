package io.redgreen.timelapse.gradle.automatedrelease

object Versioning {
  fun getNextVersion(
    displayText: String,
    isPublic: Boolean = true
  ): String {
    val version = Version.from(displayText)
    return if (isPublic) {
      version.publicRelease()
    } else {
      version.internal()
    }.displayText
  }
}
