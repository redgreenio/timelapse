import Build_gradle.DependencyVersions.junit
import Build_gradle.DependencyVersions.mobius
import Build_gradle.DependencyVersions.moshi
import org.gradle.internal.os.OperatingSystem
import proguard.gradle.ProGuardTask

plugins {
  application
  kotlin("jvm")
  id("com.github.johnrengelman.shadow") version "6.1.0"
  id("org.openjfx.javafxplugin") version "0.0.9"
  id("org.beryx.runtime") version "1.12.1"
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

object DependencyVersions {
  internal const val mobius = "1.5.3"
  internal const val junit = "5.6.0"
  internal const val moshi = "1.11.0"
}

dependencies {
  implementation(project(":visuals"))
  implementation(project(":design"))
  implementation(project(":liftoff")) // TODO: 02/03/21 Create a new source set of exploratory tests
  implementation(project(":architecture"))

  implementation(kotlin("stdlib-jdk8"))
  implementation("org.eclipse.jgit:org.eclipse.jgit:5.9.0.202009080501-r")
  implementation("org.slf4j:slf4j-simple:1.7.30")
  implementation("com.github.mfornos:humanize-slim:1.2.2")
  implementation("org.apache.commons:commons-text:1.4")
  implementation("javax.xml.bind:jaxb-api:2.4.0-b180830.0359") {
    because("Humanize requires this dependency explicitly after Java 11 upgrade.")
  }
  implementation("io.sentry:sentry:3.1.0")

  // FIXME: 05-12-2020
  // Begin Circus: We are pulling in RxJava 2 and RxJava 3 Bridge libraries just because we need the JavaFx scheduler.
  // Something to consider is to port the `JavaFxScheduler` class to RxJava 3 so that we can remove these dependencies.
  implementation("io.reactivex.rxjava2:rxjavafx:2.2.2")
  implementation("io.reactivex.rxjava2:rxjava:2.2.20") {
    because("JavaFx bindings requires the library.")
  }
  implementation("com.github.akarnokd:rxjava3-bridge:3.0.0") {
    because("JavaFx bindings for RxJava 3 is not available.")
  }
  // End Circus

  implementation("com.squareup.moshi:moshi:$moshi")
  kapt("com.squareup.moshi:moshi-kotlin-codegen:$moshi")
  implementation("io.arrow-kt:arrow-core-data:0.11.0")
  implementation("com.github.ben-manes.caffeine:caffeine:3.0.0")
  implementation("org.greenrobot:eventbus:3.2.0") {
    because("Provides a decoupled API for analytics.")
  }

  testImplementation(testFixtures(project(":fixtures:library")))
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junit")
  testImplementation("org.junit.jupiter:junit-jupiter-params:$junit")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit")
  testImplementation("com.spotify.mobius:mobius-test:$mobius")
  testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0") {
    isTransitive = false
    because("we want the extension functions on the latest Mockito artifact.")
  }
  testImplementation("org.mockito:mockito-core:3.7.7")
  testImplementation("com.google.truth:truth:1.0.1")
  testImplementation("com.approvaltests:approvaltests:9.3.0")
}

with(application) {
  applicationName = productName
  mainClass.set("LauncherKt")
}

val useIRBackend = true

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "15"
    kotlinOptions.useIR = useIRBackend
    kotlinOptions.freeCompilerArgs = listOf("-Xinline-classes")
  }

  compileTestKotlin {
    kotlinOptions.jvmTarget = "15"
    kotlinOptions.useIR = useIRBackend
    kotlinOptions.freeCompilerArgs = listOf("-Xinline-classes")
  }

  test {
    useJUnitPlatform()
  }

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

/*
tasks.jacocoTestReport {
  reports {
    xml.isEnabled = true
  }

  dependsOn(tasks.test)
}
*/
