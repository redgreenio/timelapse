import Build_gradle.DependencyVersions.junit
import Build_gradle.DependencyVersions.mobius
import proguard.gradle.ProGuardTask

plugins {
  application
  kotlin("jvm")
  id("com.github.johnrengelman.shadow") version "6.1.0"
  id("org.openjfx.javafxplugin") version "0.0.9"
}

group = "io.redgreen"
version = "0.1.7"

javafx {
  version = "11"
  modules("javafx.controls", "javafx.web")
}

object DependencyVersions {
  internal const val mobius = "1.5.3"
  internal const val junit = "5.6.0"
}

dependencies {
  implementation(project(":visuals"))

  implementation(kotlin("stdlib-jdk8"))
  implementation("org.eclipse.jgit:org.eclipse.jgit:5.9.0.202009080501-r")
  implementation("org.slf4j:slf4j-simple:1.7.30")
  implementation("com.github.mfornos:humanize-slim:1.2.2")
  implementation("com.spotify.mobius:mobius-core:$mobius")
  implementation("com.spotify.mobius:mobius-rx3:$mobius")
  implementation("com.spotify.mobius:mobius-extras:$mobius")
  implementation("org.apache.commons:commons-text:1.4")
  implementation("javax.xml.bind:jaxb-api:2.4.0-b180830.0359") {
    because("Humanize requires this dependency explicitly after Java 11 upgrade.")
  }

  testImplementation("io.arrow-kt:arrow-core-data:0.11.0")
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junit")
  testImplementation("org.junit.jupiter:junit-jupiter-params:$junit")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit")
  testImplementation("com.spotify.mobius:mobius-test:$mobius")
  testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
  testImplementation("com.google.truth:truth:1.0.1")
  testImplementation("com.approvaltests:approvaltests:9.3.0")
}

with(application) {
  applicationName = "timelapse"
  mainClassName = "LauncherKt"
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }

  compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }

  test {
    useJUnitPlatform()
  }

  register<ProGuardTask>("demoJar") {
    dependsOn("shadowJar")
    val shadowJar = "build/libs/timelapse-${version}-all.jar"
    val demoJar = "build/libs/timelapse-${version}-demo.jar"
    val mappingFile = "build/libs/mapping-${version}.txt"

    // Library jars
    val javaHome = System.getProperty("java.home")
    val javaRuntime = "$javaHome/jmods"
    val javaCryptographicExtensions = "$javaHome/lib/jce.jar"

    configuration("proguard-rules.pro")

    injars(shadowJar)
    outjars(demoJar)

    libraryjars(javaRuntime)
    libraryjars(javaCryptographicExtensions)

    printmapping(mappingFile)

    doLast { delete(shadowJar) }
  }
}

tasks.jacocoTestReport {
  reports {
    xml.isEnabled = true
  }

  dependsOn(tasks.test)
}
