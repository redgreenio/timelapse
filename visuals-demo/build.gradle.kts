plugins {
  id("timelapse-javafx")
}

javafx {
  modules("javafx.controls", "javafx.web")
}

dependencies {
  implementation(project(":visuals"))
  implementation(project(":design"))
}
