package io.redgreen.timelapse.gradle

import io.redgreen.timelapse.gradle.automatedrelease.Versioning
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.logging.text.StyledTextOutput
import org.gradle.internal.logging.text.StyledTextOutputFactory
import org.gradle.kotlin.dsl.support.serviceOf

class AutomatedReleasePlugin : Plugin<Project> {
  override fun apply(target: Project) {
    val output = target.serviceOf<StyledTextOutputFactory>().create("version-output")

    target.tasks.register("nextPublicVersion") {
      val isPublic = true
      val versionQualifier = "PUBLIC"
      description = "Prints the version text for the next ${versionQualifier.toLowerCase()} release."
      val nextVersion = Versioning.getNextVersion(getLatestVersion(), isPublic)
      val message = "Next $versionQualifier version should be: "

      doLast {
        output
          .style(StyledTextOutput.Style.Success)
          .text(message)
          .style(StyledTextOutput.Style.SuccessHeader)
          .text(nextVersion)
          .println()
      }
    }

    target.tasks.register("nextInternalVersion") {
      val isPublic = false
      val versionQualifier = "INTERNAL"
      description = "Prints the version text for the next ${versionQualifier.toLowerCase()} release."
      val nextVersion = Versioning.getNextVersion(getLatestVersion(), isPublic)
      val message = "Next $versionQualifier version should be: "

      doLast {
        output
          .style(StyledTextOutput.Style.Success)
          .text(message)
          .style(StyledTextOutput.Style.SuccessHeader)
          .text(nextVersion)
          .println()
      }
    }
  }

  private fun getLatestVersion(): String {
    // TODO Get the latest version from Git
    return "2021.0.1"
  }
}
