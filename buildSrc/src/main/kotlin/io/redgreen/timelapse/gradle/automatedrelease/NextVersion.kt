package io.redgreen.timelapse.gradle.automatedrelease

import io.redgreen.timelapse.gradle.automatedrelease.versions.InternalVersion
import io.redgreen.timelapse.gradle.automatedrelease.versions.ReleaseVersion

interface NextVersion {
  fun public(): ReleaseVersion
  fun internal(): InternalVersion
}
