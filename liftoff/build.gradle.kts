plugins {
  `java-library`
  id("org.openjfx.javafxplugin") version "0.0.9"
}

javafx {
  version = "15"
  modules("javafx.controls", "javafx.web")
}

dependencies {
  implementation(project(":design"))
  implementation(project(":architecture"))

  api(deps.controlsFx)
  implementation(deps.jgit)
}
