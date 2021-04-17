package io.redgreen.timelapse.gradle.automatedrelease.tasks

import io.redgreen.timelapse.gradle.automatedrelease.Versioning
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.logging.text.StyledTextOutput
import org.gradle.internal.logging.text.StyledTextOutputFactory
import org.gradle.kotlin.dsl.support.serviceOf

abstract class PrintVersionTask(private val isPublic: Boolean) : DefaultTask() {
  @TaskAction
  fun perform() {
    val versionQualifier = getVersionQualifier(isPublic)
    val nextVersion = Versioning.getNextVersion(getLatestVersion(), isPublic)
    val message = "Next $versionQualifier version should be: "

    val output = project.serviceOf<StyledTextOutputFactory>().create("version-output")
    printVersion(output, message, nextVersion)
  }

  private fun getVersionQualifier(isPublic: Boolean): String {
    return if (isPublic) {
      "PUBLIC"
    } else {
      "INTERNAL"
    }
  }

  private fun getLatestVersion(): String {
    // TODO Get the latest version from Git
    return "2021.0.1"
  }

  private fun printVersion(
    output: StyledTextOutput,
    message: String,
    nextVersion: String
  ) {
    output
      .style(StyledTextOutput.Style.Success)
      .text(message)
      .style(StyledTextOutput.Style.SuccessHeader)
      .text(nextVersion)
      .println()
  }
}
