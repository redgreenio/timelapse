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

  fun getLatestTag(
    tags: List<String>,
    yyyy: Int
  ): String {
    val shortlistedTags = tags
      .filter { it.startsWith("$yyyy") }
      .sortedDescending()

    if (shortlistedTags.isNotEmpty()) {
      return shortlistedTags.first()
    }

    return "$yyyy"
  }
}
