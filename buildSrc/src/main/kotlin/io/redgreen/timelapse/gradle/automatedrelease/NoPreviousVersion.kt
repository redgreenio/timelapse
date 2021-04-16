package io.redgreen.timelapse.gradle.automatedrelease

class NoPreviousVersion(yyyy: Int) : Version(yyyy, UNSPECIFIED, UNSPECIFIED) {
  override val displayText: String
    get() = "$yyyy"

  override fun next(isPublic: Boolean): Version {
    return if (isPublic) {
      ReleaseVersion(yyyy, 1)
    } else {
      InternalVersion(yyyy, 0, 1)
    }
  }
}
