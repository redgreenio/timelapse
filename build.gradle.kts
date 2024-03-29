repositories {
  mavenCentral()
}

plugins {
  id("timelapse")
}

buildscript {
  apply(from = ".buildscripts/git-hooks.gradle")
  repositories {
    jcenter()
  }

  dependencies {
    classpath("net.sf.proguard:proguard-gradle:6.2.2")
  }
}
