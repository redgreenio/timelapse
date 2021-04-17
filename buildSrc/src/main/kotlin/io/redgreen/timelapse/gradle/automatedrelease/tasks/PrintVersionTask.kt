package io.redgreen.timelapse.gradle.automatedrelease.tasks

import io.redgreen.timelapse.gradle.automatedrelease.Versioning
import java.time.LocalDate
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
    return Versioning.getLatestTag(tags(), LocalDate.now().year)
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

  private fun tags(): List<String> {
    return getRawTagOutput()
      .split("\n")
      .filter { it.isNotEmpty() }
      .map { it.trim() }
  }

  private fun getRawTagOutput(): String {
    return Runtime
      .getRuntime()
      .exec("git tag")
      .inputStream
      .reader()
      .use { it.readText() }
  }
}
