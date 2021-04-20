package io.redgreen.timelapse.gradle.automatedrelease

import io.redgreen.timelapse.gradle.automatedrelease.versions.InternalVersion
import io.redgreen.timelapse.gradle.automatedrelease.versions.PublicReleaseVersion

interface Next {
  fun publicRelease(): PublicReleaseVersion
  fun internal(): InternalVersion
}
