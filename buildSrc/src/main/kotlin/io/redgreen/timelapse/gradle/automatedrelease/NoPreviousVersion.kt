package io.redgreen.timelapse.gradle.automatedrelease

class NoPreviousVersion(yyyy: Int) : Version(yyyy, UNSPECIFIED, UNSPECIFIED) {
  override val displayText: String
    get() = "$yyyy"

  override fun internal(): InternalVersion {
    return InternalVersion(yyyy, 0, 1)
  }

  override fun public(): ReleaseVersion {
    return ReleaseVersion(yyyy, 1)
  }
}
