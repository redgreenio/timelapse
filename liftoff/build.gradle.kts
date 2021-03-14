plugins {
  id("timelapse-javafx")
}

javafx {
  version = "15"
  modules("javafx.controls")
}

dependencies {
  implementation(project(":design"))
  implementation(project(":architecture"))
  implementation(project(":git"))

  api(deps.controlsFx)
  implementation(deps.jgit)
}
