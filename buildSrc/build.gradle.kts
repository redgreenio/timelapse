import java.net.URI

repositories {
  mavenCentral()
  mavenCentral { url = URI.create("https://plugins.gradle.org/m2/") }
  gradlePluginPortal()
}

plugins {
  kotlin("jvm") version "1.4.32"
  `java-gradle-plugin`
  `kotlin-dsl`
}

dependencies {
  implementation(kotlin("gradle-plugin", "1.4.32"))
  implementation("com.github.ben-manes:gradle-versions-plugin:0.38.0")
  implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.16.0")
  implementation("org.openjfx:javafx-plugin:0.0.9")
  implementation("org.jlleitschuh.gradle:ktlint-gradle:10.0.0")

  testImplementation(kotlin("test-junit5"))
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
  testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
  testImplementation("com.google.truth:truth:1.1.2")
}

tasks.test {
  useJUnitPlatform()
}

gradlePlugin {
  plugins {
    create("TimelapsePlugin") {
      id = "timelapse"
      implementationClass = "io.redgreen.timelapse.gradle.TimelapsePlugin"
    }

    create("TimelapseJavaFxPlugin") {
      id = "timelapse-javafx"
      implementationClass = "io.redgreen.timelapse.gradle.TimelapseJavaFxPlugin"
    }
  }
}
