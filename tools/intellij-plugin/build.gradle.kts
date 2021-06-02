import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.jetbrains.intellij") version "0.7.3"
}

intellij {
  version = "211.7142.45"
  setPlugins("org.jetbrains.kotlin")
}

tasks.withType<KotlinCompile>().all {
  kotlinOptions {
    jvmTarget = "11"
  }
}
