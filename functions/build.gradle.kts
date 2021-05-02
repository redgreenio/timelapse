dependencies {
  implementation(deps.log4j.api)
  implementation(deps.log4j.core)
  implementation(projects.languages)

  testImplementation(deps.test.junit.api)
  testRuntimeOnly(deps.test.junit.engine)

  testImplementation(deps.test.truth)
}
