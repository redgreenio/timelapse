package io.redgreen.timelapse.gradle

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
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
      configureVersions()
      configureDetekt()
    }
  }

  private fun Project.configureRepositories() {
    repositories {
      mavenCentral {
        content {
          // just allow to include kotlinx projects
          // detekt needs 'kotlinx-html' for the html report
          includeGroup("org.jetbrains.kotlinx")
        }
      }
      jcenter()
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

  private fun Project.configureVersions() {
    apply { plugin("com.github.ben-manes.versions") }

    tasks.withType<DependencyUpdatesTask> {
      rejectVersionIf {
        val versionName = candidate.version.toLowerCase()
        versionName.endsWith("-m1")
            || versionName.contains("-ea")
            || versionName.contains("-rc")
            || versionName.contains("-alpha")
      }
    }
  }

  private fun Project.configureDetekt() {
    apply { plugin("io.gitlab.arturbosch.detekt") }

    tasks.withType<DetektCreateBaselineTask> {
      baseline.set(file("$projectDir/detekt/baseline.xml"))
    }

    tasks.withType<Detekt> {
      jvmTarget = "15"
      baseline.set(file("$projectDir/detekt/baseline.xml"))

      with(reports) {
        xml.enabled = true
        html.enabled = true
        txt.enabled = false
      }
    }
  }
}
