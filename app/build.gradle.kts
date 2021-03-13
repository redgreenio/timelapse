import org.gradle.internal.os.OperatingSystem
import proguard.gradle.ProGuardTask

plugins {
  application
  id("com.github.johnrengelman.shadow") version "6.1.0"
  id("org.openjfx.javafxplugin") version "0.0.9"
  id("org.beryx.runtime") version "1.12.2"
  kotlin("kapt")
}

val productName = "Timelapse Studio"
val vendorName = "Red Green, Inc."

group = "io.redgreen"
version = "1.0.0" /* Because, macOS apps can't have version numbers starting with '0'. */

javafx {
  version = "15"
  modules("javafx.controls", "javafx.web")
}

dependencies {
  implementation(project(":visuals"))
  implementation(project(":design"))
  implementation(project(":liftoff")) // TODO: 02/03/21 Create a new source set of exploratory tests
  implementation(project(":architecture"))
  implementation(project(":git"))

  implementation(kotlin("stdlib-jdk8"))
  implementation(deps.jgit)
  implementation(deps.slf4jSimple) {
    because("jgit prints a ton of log statements.")
  }

  implementation(deps.humanize.slim)
  implementation(deps.humanize.jaxbApi) {
    because("Humanize requires this dependency explicitly after Java 11 upgrade.")
  }

  implementation(deps.commonsText)
  implementation(deps.sentry)

  // FIXME: 05-12-2020
  // Begin Circus: We are pulling in RxJava 2 and RxJava 3 Bridge libraries just because we need the JavaFx scheduler.
  // Something to consider is to port the `JavaFxScheduler` class to RxJava 3 so that we can remove these dependencies.
  implementation(deps.rxJava2.javaFx)
  implementation(deps.rxJava2.runtime) {
    because("JavaFx bindings requires the library.")
  }
  implementation(deps.rxJava3.bridge) {
    because("JavaFx bindings for RxJava 3 is not available.")
  }
  // End Circus

  implementation(deps.moshi.runtime)
  kapt(deps.moshi.apt)

  implementation(deps.arrow.coreData)
  implementation(deps.caffeine)
  implementation(deps.eventBus) {
    because("Provides a decoupled API for analytics.")
  }

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

  testImplementation(deps.test.approvalTests)

  testImplementation(deps.mobius.test)
}

with(application) {
  applicationName = productName
  mainClass.set("LauncherKt")
}

tasks {
  register<ProGuardTask>("demoJar") {
    dependsOn("shadowJar")
    val shadowJar = "build/libs/app-${version}-all.jar"
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

runtime {
  addOptions("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages")
  launcher {
    noConsole = true
  }

  jpackage {
    val currentOs = OperatingSystem.current()
    val imageType = if (currentOs.isWindows) "ico" else if (currentOs.isMacOsX) "icns" else "png"
    imageOptions.addAll(listOf("--icon", "src/main/resources/app-icons/icon.$imageType"))

    with(installerOptions) {
      addAll(listOf("--name", productName))
      addAll(listOf("--vendor", vendorName))
      addAll(listOf("--copyright", "Copyright 2021 $vendorName"))
      addAll(listOf("--app-version", "${project.version}"))
      addAll(listOf("--resource-dir", "src/main/resources"))
    }

    when {
      currentOs.isWindows -> {
        installerOptions.addAll(listOf("--win-per-user-install", "--win-dir-chooser", "--win-menu", "--win-shortcut"))
      }

      currentOs.isLinux -> {
        installerOptions.addAll(listOf("--linux-package-name", productName, "--linux-shortcut"))
      }

      currentOs.isMacOsX -> {
        imageName = productName
        installerOutputDir = File("$projectDir/build/distribution")
        installerType = "dmg"
        with(installerOptions) {
          addAll(listOf("--mac-package-name", productName))
          addAll(listOf("--mac-package-identifier", "io.redgreen.timelapseStudio"))
        }
      }
    }
  }
}
