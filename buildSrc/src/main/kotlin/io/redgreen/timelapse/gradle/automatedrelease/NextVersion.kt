package io.redgreen.timelapse.gradle.automatedrelease

import io.redgreen.timelapse.gradle.automatedrelease.versions.InternalVersion

interface NextVersion {
  fun public(): ReleaseVersion
  fun internal(): InternalVersion
}
