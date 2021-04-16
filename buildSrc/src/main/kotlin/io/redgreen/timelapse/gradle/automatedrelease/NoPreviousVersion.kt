package io.redgreen.timelapse.gradle.automatedrelease

class NoPreviousVersion(yyyy: Int, publishedArtifactCount: Int = 0) : Version("", yyyy) {
  override fun nextPublishedArtifactCount(isPublic: Boolean): Int {
    return if (isPublic) {
      1
    } else {
      0
    }
  }

  override fun getYear(): String =
    yyyy.toString()
}
