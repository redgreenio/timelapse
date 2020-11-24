repositories {
  mavenCentral()
}

plugins {
  kotlin("jvm") version "1.4.0"
}

buildscript {
  repositories {
    jcenter()
  }

  dependencies {
    classpath("net.sf.proguard:proguard-gradle:6.2.2")
  }

  subprojects {
    repositories {
      mavenCentral()
    }
  }
}
