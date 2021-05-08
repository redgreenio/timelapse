dependencies {
  implementation(deps.picocli)
  implementation(projects.design)

  testImplementation(deps.test.junit.api)
  testRuntimeOnly(deps.test.junit.engine)

  testImplementation(deps.test.approvalTests)
}
