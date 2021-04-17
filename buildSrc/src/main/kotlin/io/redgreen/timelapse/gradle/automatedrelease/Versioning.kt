package io.redgreen.timelapse.gradle.automatedrelease

object Versioning {
  fun getNextVersion(
    displayText: String,
    isPublic: Boolean = true
  ): String {
    val version = VersionDeserializer.deserialize(displayText)
    return if (isPublic) {
      version.publicRelease()
    } else {
      version.internal()
    }.displayText
  }
}
