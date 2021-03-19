plugins {
  id("timelapse-javafx")
}

javafx {
  modules("javafx.controls")
}

dependencies {
  implementation(project(":design"))
  implementation(project(":architecture"))
  implementation(project(":git"))

  api(deps.controlsFx)
  implementation(deps.jgit)
}
