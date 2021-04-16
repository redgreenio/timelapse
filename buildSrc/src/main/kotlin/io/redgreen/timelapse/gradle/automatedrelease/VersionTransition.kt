package io.redgreen.timelapse.gradle.automatedrelease

import io.redgreen.timelapse.gradle.automatedrelease.versions.InternalVersion
import io.redgreen.timelapse.gradle.automatedrelease.versions.PublicReleaseVersion

interface VersionTransition {
  fun publicRelease(): PublicReleaseVersion
  fun internal(): InternalVersion
}
