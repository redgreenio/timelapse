package io.redgreen.timelapse.gradle

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.benmanes.gradle.versions.updates.gradle.GradleReleaseChannel
import deps
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import pluginsDeps as pluginDeps

class TimelapsePlugin : Plugin<Project> {
  private val inUseFreeCompilerArgs = listOf("-Xinline-classes")

  override fun apply(target: Project) {
    target.subprojects {
      if (this.name.endsWith("-js")) {
        return@subprojects
      }

      configureRepositories()
      configureKotlin()
      configureJacoco()
      configureJUnit()
      configureVersions()
      configureDetekt()
      configureKtlint()
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
    apply { plugin(pluginDeps.kotlinJvm.id) }

    tasks.withType<KotlinCompile>().configureEach {
      kotlinOptions {
        jvmTarget = deps.versions.java
        @Suppress("SuspiciousCollectionReassignment")
        freeCompilerArgs += inUseFreeCompilerArgs
        useIR = true
      }
    }
  }

  private fun Project.configureJacoco() {
    apply { plugin(pluginDeps.jacoco.id) }

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
    fun isNonStable(version: String): Boolean {
      val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
      val regex = Regex("^[0-9,.v-]+(-r)?$")
      return !stableKeyword && !regex.matches(version)
    }

    apply { plugin(pluginDeps.versions.id) }

    tasks.withType<DependencyUpdatesTask> {
      gradleReleaseChannel = GradleReleaseChannel.CURRENT.id
      checkForGradleUpdate = true
      outputFormatter = "json"

      rejectVersionIf { isNonStable(candidate.version) }
    }
  }

  private fun Project.configureDetekt() {
    apply { plugin(pluginDeps.detekt.id) }

    tasks.withType<DetektCreateBaselineTask> {
      baseline.set(file("$projectDir/detekt/baseline.xml"))
    }

    tasks.withType<Detekt> {
      jvmTarget = deps.versions.java
      baseline.set(file("$projectDir/detekt/baseline.xml"))

      with(reports) {
        xml.enabled = true
        html.enabled = true
        txt.enabled = false
      }
    }
  }

  private fun Project.configureKtlint() {
    apply { plugin(pluginDeps.ktlint.id) }
  }
}
