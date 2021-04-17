plugins {
  id("timelapse-javafx")
}

javafx {
  modules("javafx.controls")
}

dependencies {
  implementation(projects.design)
  implementation(projects.architecture)
  implementation(projects.git)

  api(deps.controlsFx)
  implementation(deps.jgit)
}
