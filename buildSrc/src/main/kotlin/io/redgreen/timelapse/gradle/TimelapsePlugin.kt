package io.redgreen.timelapse.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.plugins
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class TimelapsePlugin : Plugin<Project> {
  private val inUseFreeCompilerArgs = listOf("-Xinline-classes")

  override fun apply(target: Project) {
    target.subprojects {
      configureRepositories()
      configureKotlin()
      configureJacoco()
      configureJUnit()
    }
  }

  private fun Project.configureRepositories() {
    repositories {
      mavenCentral()
    }
  }

  private fun Project.configureKotlin() {
    apply { plugin("org.jetbrains.kotlin.jvm") }

    tasks.withType<KotlinCompile>().configureEach {
      kotlinOptions {
        jvmTarget = "15"
        @Suppress("SuspiciousCollectionReassignment")
        freeCompilerArgs += inUseFreeCompilerArgs
        useIR = true
      }
    }
  }

  private fun Project.configureJacoco() {
    apply { plugin("jacoco") }

    tasks.withType<JacocoReport> {
      reports {
        xml.isEnabled = true
      }

      dependsOn(tasks.withType<Test>())
    }
  }

  private fun Project.configureJUnit() {
    tasks.withType<Test> {
      useJUnitPlatform()
    }
  }
}
