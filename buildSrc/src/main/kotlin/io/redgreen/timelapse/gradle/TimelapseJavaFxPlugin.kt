package io.redgreen.timelapse.gradle

import deps
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.openjfx.gradle.JavaFXOptions
import plugins

class TimelapseJavaFxPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    target.apply { plugin(plugins.openjfx.id) }

    target.configure<JavaFXOptions> {
      version = deps.versions.javaFx
    }
  }
}
