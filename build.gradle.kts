repositories {
  mavenCentral()
}

plugins {
  kotlin("jvm") version "1.4.30"
}

buildscript {
  repositories {
    jcenter()
  }

  dependencies {
    classpath("net.sf.proguard:proguard-gradle:6.2.2")
  }

  subprojects {
    apply { plugin("jacoco") }

    repositories {
      mavenCentral()
    }
  }
}
