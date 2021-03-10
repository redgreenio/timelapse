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
  // Because https://github.com/spotify/diffuser is not published to Maven Central
  api(files("libs/diffuser-0.0.5.jar"))

  api(deps.rxJava3.runtime)

  api(deps.mobius.core)
  api(deps.mobius.rx3)
  api(deps.mobius.extras)
}
