import proguard.gradle.ProGuardTask

buildscript {
  repositories {
    jcenter()
  }
  dependencies {
    classpath("net.sf.proguard:proguard-gradle:6.2.2")
  }
}

plugins {
  application
  kotlin("jvm") version "1.4.0"
  id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "io.redgreen"
version = "0.1.2"

repositories {
  mavenCentral()
}

dependencies {
  implementation(kotlin("stdlib-jdk8"))
  implementation("org.eclipse.jgit:org.eclipse.jgit:5.9.0.202009080501-r")
  implementation("org.slf4j:slf4j-simple:1.7.30")
  implementation("com.github.mfornos:humanize-slim:1.2.2")
  implementation("com.spotify.mobius:mobius-core:1.5.3")

  testImplementation("io.arrow-kt:arrow-core-data:0.11.0")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
  testImplementation("org.junit.jupiter:junit-jupiter-params:5.6.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
  testImplementation("com.spotify.mobius:mobius-test:1.5.3")

  testImplementation("com.google.truth:truth:1.0.1")
}

application.mainClassName = "LauncherKt"

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

  register<ProGuardTask>("buildDemo") {
    dependsOn("shadowJar")

    configuration("proguard-rules.pro")

    injars("build/libs/timelapse-${version}-all.jar")
    outjars("build/libs/timelapse-${version}-demo.jar")

    libraryjars("${System.getProperty("java.home")}/lib/rt.jar")
    libraryjars("${System.getProperty("java.home")}/lib/jce.jar")

    printmapping("build/libs/mapping-${version}.txt")
  }
}
