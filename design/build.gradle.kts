plugins {
  id("timelapse-javafx")
}

javafx {
  modules("javafx.controls")
}

dependencies {
  testImplementation(deps.test.junit.api)
  testImplementation(deps.test.junit.params)
  testRuntimeOnly(deps.test.junit.engine)

  testImplementation(deps.test.truth)
  testImplementation(deps.test.approvalTests)
}
