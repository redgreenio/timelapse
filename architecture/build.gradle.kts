plugins {
  id("timelapse-javafx")
}

javafx {
  modules("javafx.controls")
}

dependencies {
  // Because https://github.com/spotify/diffuser is not published to Maven Central
  api(files("libs/diffuser-0.0.5.jar"))

  api(deps.rxJava3.runtime)

  api(deps.mobius.core)
  api(deps.mobius.rx3)
  api(deps.mobius.extras)
}
