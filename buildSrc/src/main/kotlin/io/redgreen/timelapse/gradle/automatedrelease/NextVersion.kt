package io.redgreen.timelapse.gradle.automatedrelease

interface NextVersion {
  fun public(): ReleaseVersion
  fun internal(): InternalVersion
}
