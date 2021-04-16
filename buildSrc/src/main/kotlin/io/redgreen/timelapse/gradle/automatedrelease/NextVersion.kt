package io.redgreen.timelapse.gradle.automatedrelease

import io.redgreen.timelapse.gradle.automatedrelease.versions.InternalVersion
import io.redgreen.timelapse.gradle.automatedrelease.versions.PublicReleaseVersion

interface NextVersion {
  fun public(): PublicReleaseVersion
  fun internal(): InternalVersion
}
