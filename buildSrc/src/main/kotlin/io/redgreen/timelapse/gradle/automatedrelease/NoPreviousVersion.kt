package io.redgreen.timelapse.gradle.automatedrelease

class NoPreviousVersion(yyyy: Int) : Version("", yyyy) {
  override fun getYear(): String =
    yyyy.toString()
}
