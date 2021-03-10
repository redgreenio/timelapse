plugins {
  id("org.openjfx.javafxplugin") version "0.0.9"
}

javafx {
  version = "15"
  modules("javafx.controls", "javafx.web")
}

dependencies {
  implementation(project(":design"))

  testImplementation(deps.arrow.coreData)
  testImplementation(deps.test.junit.api)
  testImplementation(deps.test.junit.params)
  testRuntimeOnly(deps.test.junit.engine)

  testImplementation(deps.test.truth)

  testImplementation(deps.test.approvalTests)
}
