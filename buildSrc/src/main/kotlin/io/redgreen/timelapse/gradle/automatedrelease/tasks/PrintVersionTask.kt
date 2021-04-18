package io.redgreen.timelapse.gradle.automatedrelease.tasks

import io.redgreen.timelapse.gradle.automatedrelease.Versioning
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.logging.text.StyledTextOutput
import org.gradle.internal.logging.text.StyledTextOutputFactory
import org.gradle.kotlin.dsl.support.serviceOf

abstract class PrintVersionTask(private val isPublic: Boolean) : DefaultTask() {
  companion object {
    private const val CHANGELOG = "CHANGELOG.md"
    private val APP_BUILD_GRADLE_KTS = "app${File.separator}build.gradle.kts"

    private const val PRINT_DEBUG_INFO = false
  }

  @TaskAction
  fun perform() {
    // Step 1 - Get the next version
    val versionQualifier = getVersionQualifier(isPublic)
    val nextVersion = Versioning.getNextVersion(getLatestVersion(), isPublic)
    val message = "Next $versionQualifier version should be: "

    val output = project.serviceOf<StyledTextOutputFactory>().create("version-output")
    printVersion(output, message, nextVersion)

    // Step 2 - Update CHANGELOG.md
    val updatedChangeLog = getUpdatedChangelogText(nextVersion)
    if (PRINT_DEBUG_INFO) {
      output.println(updatedChangeLog)
    }

    // Step 3 - Update version in buildscript
    val buildGradleKts = getUpdatedBuildGradleKts(nextVersion)
    if (PRINT_DEBUG_INFO) {
      output.println(buildGradleKts)
    }
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

  private fun getUpdatedChangelogText(version: String): String {
    return File(CHANGELOG)
      .readText()
      .replace(
        """
            |## [Unreleased]
            |
            """.trimMargin("|"),
        """
            |## [Unreleased]
            |
            |## [$version] - ${LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)}
            |
            """.trimMargin("|")
      )
  }

  private fun getUpdatedBuildGradleKts(version: String): String {
    return File(APP_BUILD_GRADLE_KTS)
      .readText()
      .replace(
        """
            |group = "io.redgreen"
            |version = .*
            |
            """.trimIndent(),
        """
            |group = "io.redgreen"
            |version = "$version"
            |
            """.trimIndent()
      )
  }
}
