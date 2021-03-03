plugins {
  `java-library`
  kotlin("jvm")
  id("org.openjfx.javafxplugin") version "0.0.9"
}

javafx {
  version = "15"
  modules("javafx.controls", "javafx.web")
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "15"
  }

  compileTestKotlin {
    kotlinOptions.jvmTarget = "15"
  }
}

dependencies {
  implementation(project(":design"))
  implementation(project(":architecture"))
  api("org.controlsfx:controlsfx:11.1.0")
  implementation("org.eclipse.jgit:org.eclipse.jgit:5.9.0.202009080501-r")
}
