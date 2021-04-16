package io.redgreen.timelapse.gradle.automatedrelease

class NoPreviousVersion(private val yyyy: Int) : Version {
  override val displayText: String
    get() = "$yyyy"

  override fun internal(): InternalVersion {
    return InternalVersion(yyyy, 0, 1)
  }

  override fun public(): ReleaseVersion {
    return ReleaseVersion(yyyy, 1)
  }
}
