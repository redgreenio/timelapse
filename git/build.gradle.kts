dependencies {
  implementation(deps.arrow.coreData)
  implementation(deps.jgit)

  testImplementation(testFixtures(project(":fixtures:library")))

  testImplementation(deps.test.junit.api)
  testImplementation(deps.test.junit.params)
  testRuntimeOnly(deps.test.junit.engine)

  testImplementation(deps.test.truth)

  testImplementation(deps.test.mockito.core)
  testImplementation(deps.test.mockito.kotlin) {
    isTransitive = false
    because("we want extension functions on the 'latest' Mockito artifact.")
  }
}
