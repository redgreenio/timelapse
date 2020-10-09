plugins {
  kotlin("jvm") version "1.4.0"
}

group = "io.redgreen"
version = "0.1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  implementation(kotlin("stdlib-jdk8"))
  implementation("info.picocli:picocli:4.5.1")
  implementation("org.eclipse.jgit:org.eclipse.jgit:5.9.0.202009080501-r")

  testImplementation("io.arrow-kt:arrow-core-data:0.11.0")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
  testImplementation("org.junit.jupiter:junit-jupiter-params:5.6.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")

  testImplementation("com.google.truth:truth:1.0.1")
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }

  compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }

  test {
    useJUnitPlatform()
  }
}
