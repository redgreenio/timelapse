plugins {
  `java-library`
  kotlin("jvm")
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "15"
  }

  compileTestKotlin {
    kotlinOptions.jvmTarget = "15"
  }
}

dependencies {
  implementation("io.reactivex.rxjava3:rxjava:3.0.10")
}
