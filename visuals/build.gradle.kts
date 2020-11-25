import Build_gradle.DependencyVersions.junit

plugins {
  `java-library`
  kotlin("jvm")
  id("org.openjfx.javafxplugin") version "0.0.9"
}

javafx {
  version = "11"
  modules("javafx.controls", "javafx.web")
}

object DependencyVersions {
  internal const val junit = "5.6.0"
}

dependencies {
  testImplementation("io.arrow-kt:arrow-core-data:0.11.0")
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junit")
  testImplementation("org.junit.jupiter:junit-jupiter-params:$junit")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit")
  testImplementation("com.google.truth:truth:1.0.1")
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }

  compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }

  test {
    useJUnitPlatform()
  }
}
