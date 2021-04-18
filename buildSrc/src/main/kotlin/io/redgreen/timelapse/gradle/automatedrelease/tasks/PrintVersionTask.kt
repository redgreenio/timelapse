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

    private val APP_BUILD_GRADLE_KTS = "app/build.gradle.kts"
      .replace("/", File.separator)

    private val TIMELAPSE_APP = "app/src/main/kotlin/io/redgreen/timelapse/TimelapseApp.kt"
      .replace("/", File.separator)

    private const val DO_REAL_WORLD_THINGS = false
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
    if (DO_REAL_WORLD_THINGS) {
      writeToFile(CHANGELOG, updatedChangeLog)
      output.println(updatedChangeLog)
    }

    // Step 3 - Update version in buildscript
    val buildGradleKts = getUpdatedBuildGradleKts(nextVersion)
    if (DO_REAL_WORLD_THINGS) {
      writeToFile(APP_BUILD_GRADLE_KTS, buildGradleKts)
      output.println(buildGradleKts)
    }

    // Step 4 - Update version in TimelapseApp.kt
    val timelapseAppKt = getUpdatedTimelapseApp(nextVersion)
    if (DO_REAL_WORLD_THINGS) {
      writeToFile(TIMELAPSE_APP, timelapseAppKt)
      output.println(timelapseAppKt)
    }

    // Step 5 - Stage and commit
    if (DO_REAL_WORLD_THINGS) {
      Runtime.getRuntime().exec(arrayOf("git", "add", "."))
      Runtime.getRuntime().exec(arrayOf("git", "commit", "-m", "'build(incubation): bump version for release'"))
    }

    // Step 6 - Create a native distribution
    if (DO_REAL_WORLD_THINGS) {
      Runtime.getRuntime().exec(arrayOf("gradlew", "jpackageImage", "jpackage"))
    }

    // Step 7 - Tag the release
    if (DO_REAL_WORLD_THINGS) {
      Runtime.getRuntime().exec(arrayOf("git", "tag", nextVersion))
      Runtime.getRuntime().exec(arrayOf("git", "push", "origin", nextVersion))
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
    return readFile(CHANGELOG)
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

  private fun getUpdatedTimelapseApp(version: String): String {
    return readFile(TIMELAPSE_APP)
      .replace(
        """
            |private const val APP_VERSION = .*
            |
            """.trimIndent(),
        """
            |private const val APP_VERSION = "$version" 
            |
            """.trimIndent()
      )
  }

  private fun getUpdatedBuildGradleKts(version: String): String {
    return readFile(APP_BUILD_GRADLE_KTS)
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

  private fun readFile(filePath: String): String {
    return File(filePath)
      .readText()
  }

  private fun writeToFile(filePath: String, content: String) {
    File(filePath).writeText(content)
  }
}
