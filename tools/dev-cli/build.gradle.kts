import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  application
  id("com.github.johnrengelman.shadow") version "7.0.0"
}

application {
  mainClassName = "io.redgreen.timelapse.devcli.MainKt"
}

dependencies {
  implementation(projects.design)

  implementation(deps.picocli)
  implementation(deps.commonsText)
  implementation(deps.jansi)

  testImplementation(deps.test.junit.api)
  testRuntimeOnly(deps.test.junit.engine)

  testImplementation(deps.test.approvalTests)
}

tasks {
  named<ShadowJar>("shadowJar") {
    archiveBaseName.set("dev-cli")
    mergeServiceFiles()

    manifest {
      attributes(mapOf("Main-Class" to "io.redgreen.timelapse.devcli.MainKt"))
    }
  }
}

tasks {
  build {
    dependsOn(shadowJar)
  }
}

tasks.register("executable", DefaultTask::class) {
  description = "Creates self-executable file, that runs generated shadow jar"
  group = "Distribution"

  inputs.files(tasks.named("shadowJar"))
  outputs.file("${buildDir.resolve("exec").resolve("time")}")

  doLast {
    val execFile = outputs.files.singleFile

    execFile.outputStream().use {
      it.write("#!/bin/sh\n\nexec java -Xmx512m -jar \"\$0\" \"\$@\"\n\n".toByteArray())
      it.write(inputs.files.singleFile.readBytes())
    }
  }
}
