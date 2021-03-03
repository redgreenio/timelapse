import Build_gradle.DependencyVersions.mobius

plugins {
  `java-library`
  kotlin("jvm")
  id("org.openjfx.javafxplugin") version "0.0.9"
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "15"
  }

  compileTestKotlin {
    kotlinOptions.jvmTarget = "15"
  }
}

javafx {
  version = "15"
  modules("javafx.controls")
}

object DependencyVersions {
  internal const val mobius = "1.5.3"
}

dependencies {
  api("io.reactivex.rxjava3:rxjava:3.0.10")

  api("com.spotify.mobius:mobius-core:$mobius")
  api("com.spotify.mobius:mobius-rx3:$mobius")
  api("com.spotify.mobius:mobius-extras:$mobius")
}
