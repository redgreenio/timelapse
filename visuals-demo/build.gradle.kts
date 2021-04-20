plugins {
  id("timelapse-javafx")
}

javafx {
  modules("javafx.controls", "javafx.web")
}

dependencies {
  implementation(projects.visuals)
  implementation(projects.design)
}
