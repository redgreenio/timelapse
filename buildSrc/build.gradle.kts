repositories {
  mavenCentral()
  gradlePluginPortal()
}

plugins {
  kotlin("jvm") version "1.4.31"
  `java-gradle-plugin`
  `kotlin-dsl`
}

dependencies {
  implementation(kotlin("gradle-plugin", "1.4.31"))
  implementation("com.github.ben-manes:gradle-versions-plugin:0.38.0")
}

gradlePlugin {
  plugins {
    create("TimelapsePlugin") {
      id = "timelapse"
      implementationClass = "io.redgreen.timelapse.gradle.TimelapsePlugin"
    }
  }
}
