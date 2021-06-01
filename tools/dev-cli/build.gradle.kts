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
  implementation(projects.git)
  implementation(projects.languages)

  implementation(deps.picocli)
  implementation(deps.commonsText)
  implementation(deps.jansi)

  testImplementation(deps.test.junit.api)
  testRuntimeOnly(deps.test.junit.engine)
  testImplementation(deps.test.truth)

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

tasks.register("generateTemplateHtml", DefaultTask::class) {
  description = "Creates template HTML from `extended-diff.css` and `extended-diff.js`."

  inputs.files(rootDir.resolve("app/src/main/resources/xd/xd.css"))
  inputs.files(rootDir.resolve("app/src/main/resources/xd/xd.js"))
  inputs.files(rootDir.resolve("xd-js/src/static/css/xd-interaction.css"))
  inputs.files(rootDir.resolve("xd-js/src/xd.js"))
  inputs.files(projectDir.resolve("src/main/resources/template-skeleton.html"))

  outputs.file(projectDir.resolve("src/main/resources/template.html"))

  doLast {
    val allInputFiles = inputs.files.files.toList()
    val extendedDiffCss = allInputFiles[0]
    val extendedDiffJs = allInputFiles[1]
    val extendedDiffInteractionCss = allInputFiles[2]
    val extendedDiffInteractionJs = allInputFiles[3]
    val templateSkeletonHtml = allInputFiles[4]

    val combinedCss = extendedDiffCss.readText() + extendedDiffInteractionCss.readText()
    val combinedJs = extendedDiffJs.readText() + cleanupNodeJsCode(extendedDiffInteractionJs)
    val templateHtml = templateSkeletonHtml
      .readText()
      .replace("/*{css}*/", padLeft(combinedCss))
      .replace("/*{js}*/", padLeft(combinedJs))

    val templateHtmlFile = outputs.files.singleFile
    templateHtmlFile.outputStream().use {
      it.write(templateHtml.toByteArray())
    }
  }
}

fun padLeft(fileContents: String): String {
  val isWindows = System.getProperty("os.name").toLowerCase().contains("windows")
  val newlineChar = if (isWindows) "\r\n" else "\n"

  return fileContents
    .trim()
    .split(newlineChar)
    .joinToString(newlineChar) { if (it.isNotBlank()) "    $it" else it }
}

tasks
  .findByName("compileKotlin")
  ?.dependsOn(tasks.findByName("generateTemplateHtml"))

tasks.register("executable", DefaultTask::class) {
  description = "Creates self-executable file, that runs generated shadow jar"
  group = "Distribution"

  inputs.files(tasks.named("shadowJar"))
  outputs.file("${buildDir.resolve("exec").resolve("dev-cli")}")

  doLast {
    val execFile = outputs.files.singleFile

    execFile.outputStream().use {
      it.write("#!/bin/sh\n\nexec java -Xmx512m -jar \"\$0\" \"\$@\"\n\n".toByteArray())
      it.write(inputs.files.singleFile.readBytes())
    }
  }
}

fun cleanupNodeJsCode(jsFile: File): String {
  val jsWithNode = jsFile.readText()
  val nodeJsCodeStartIndex = jsWithNode.indexOf("module.exports")
  return jsWithNode.substring(0, nodeJsCodeStartIndex)
}
