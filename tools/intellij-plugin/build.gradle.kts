import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.jetbrains.intellij") version "0.7.3"
}

intellij {
  version = "2020.1"
}

tasks.withType<KotlinCompile>().all {
  kotlinOptions {
    jvmTarget = "11"
  }
}
