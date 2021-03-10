plugins {
  `java-library`
  kotlin("jvm")
  id("org.openjfx.javafxplugin") version "0.0.9"
}

javafx {
  version = "15"
  modules("javafx.controls", "javafx.web")
}

object DependencyVersions {
  internal const val junit = "5.6.0"
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

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "15"
  }

  compileTestKotlin {
    kotlinOptions.jvmTarget = "15"
  }

  test {
    useJUnitPlatform()
  }
}

tasks.jacocoTestReport {
  reports {
    xml.isEnabled = true
  }

  dependsOn(tasks.test)
}
