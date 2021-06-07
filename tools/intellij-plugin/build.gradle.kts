import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.jetbrains.intellij") version "0.7.3"
}

intellij {
  version = "211.7142.45"
  setPlugins("java", "org.jetbrains.kotlin")
}

tasks.withType<KotlinCompile>().all {
  kotlinOptions {
    jvmTarget = "11"
  }
}

dependencies {
  // JUnit 5
  testImplementation(deps.test.junit.api)
  testImplementation(deps.test.junit.params)
  testRuntimeOnly(deps.test.junit.engine)
  testRuntimeOnly(deps.test.junit.vintageEngine)

  // Truth
  testImplementation(deps.test.truth)

  // Mockito
  testImplementation(deps.test.mockito.core)
  testImplementation(deps.test.mockito.kotlin)

  testImplementation(deps.test.approvalTests)
}
