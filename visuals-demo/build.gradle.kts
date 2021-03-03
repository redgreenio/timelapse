plugins {
  application
  kotlin("jvm")
  id("org.openjfx.javafxplugin") version "0.0.9"
}

javafx {
  version = "15"
  modules("javafx.controls", "javafx.web")
}

application.mainClassName = "io.redgreen.visuals.demo.VisualsDemoApp"

dependencies {
  implementation(project(":visuals"))
  implementation(project(":design"))
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "15"
  }

  compileTestKotlin {
    kotlinOptions.jvmTarget = "15"
  }
}
