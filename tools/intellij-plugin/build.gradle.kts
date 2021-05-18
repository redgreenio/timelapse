import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.jetbrains.intellij") version "0.7.3"
}

tasks.withType<KotlinCompile>().all {
  kotlinOptions {
    jvmTarget = "11"
  }
}
