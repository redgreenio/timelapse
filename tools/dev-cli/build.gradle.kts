dependencies {
  implementation(projects.design)

  implementation(deps.picocli)
  implementation(deps.commonsText)

  testImplementation(deps.test.junit.api)
  testRuntimeOnly(deps.test.junit.engine)

  testImplementation(deps.test.approvalTests)
}
