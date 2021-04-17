package io.redgreen.timelapse.gradle

import io.redgreen.timelapse.gradle.automatedrelease.Versioning
import org.gradle.api.Plugin
import org.gradle.api.Project

class AutomatedReleasePlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.tasks.register("nextPublicVersion") {
      description = "Prints the version text for the next public release."
      doLast {
        println("Next PUBLIC version should be: ${Versioning.getNextVersion(getLatestVersion(), true)}")
      }
    }

    target.tasks.register("nextInternalVersion") {
      description = "Prints the version text for the next internal release."
      doLast {
        println("Next INTERNAL version should be: ${Versioning.getNextVersion(getLatestVersion(), false)}")
      }
    }
  }

  private fun getLatestVersion(): String {
    // TODO Get the latest version from Git
    return "2021.0.1"
  }
}
