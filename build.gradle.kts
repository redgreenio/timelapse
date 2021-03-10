repositories {
  mavenCentral()
}

plugins {
  id("timelapse")
}

buildscript {
  repositories {
    jcenter()
  }

  dependencies {
    classpath("net.sf.proguard:proguard-gradle:6.2.2")
  }
}
