plugins {
  application
  kotlin("jvm")
  id("org.openjfx.javafxplugin") version "0.0.9"
}

javafx {
  version = "11"
  modules("javafx.controls", "javafx.web")
}

application.mainClassName = "io.redgreen.visuals.demo.VisualsDemoApp"

dependencies {
  implementation(project(":visuals"))
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "11"
  }

  compileTestKotlin {
    kotlinOptions.jvmTarget = "11"
  }
}
