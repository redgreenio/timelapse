package io.redgreen.timelapse.gradle

import io.redgreen.timelapse.gradle.automatedrelease.tasks.PrintVersionTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

open class NextPublicVersionTask : PrintVersionTask(true)

open class NextInternalVersionTask : PrintVersionTask(false)

class AutomatedReleasePlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.tasks.register<NextPublicVersionTask>("nextPublicVersion")
    target.tasks.register<NextInternalVersionTask>("nextInternalVersion")
  }
}
