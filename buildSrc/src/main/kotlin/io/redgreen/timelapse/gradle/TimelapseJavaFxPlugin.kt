package io.redgreen.timelapse.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.openjfx.gradle.JavaFXOptions

class TimelapseJavaFxPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.apply { plugin("org.openjfx.javafxplugin") }

    val javaFxOptions = target.extensions.findByName("javafx") as JavaFXOptions
    javaFxOptions.version = "16"
  }
}
