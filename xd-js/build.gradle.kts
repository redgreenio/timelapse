import com.github.gradle.node.npm.task.NpmTask

plugins {
  id("com.github.node-gradle.node") version "3.1.0"
}

node {
  download.set(true)
  version.set("14.17.0")
}

registerNpmTask("testJs", "test")
registerNpmTask("eslintCheck", "run", "eslintCheck")
registerNpmTask("eslintFix", "run", "eslintFix")

fun registerNpmTask(name: String, command: String, vararg commandArgs: String) {
  tasks.register(name, NpmTask::class) {
    dependsOn(tasks.findByName("npmInstall"))
    args.set(listOf(command, *commandArgs))
  }
}

tasks.register("build", DefaultTask::class) {
  description = "Prepares Node.Js code for the browser."

  inputs.files(projectDir.resolve("src/xd.js"))
  inputs.files(projectDir.resolve("src/static/xd.js.bootstrap"))

  outputs.file(projectDir.resolve("build/xd-browser.js"))

  doLast {
    val allInputFiles = inputs.files.files.toList()
    val xdJs = allInputFiles[0]
    val xdJsBootstrap = allInputFiles[1]

    val browserJs = xdJsBootstrap
      .readText()
      .replace("/*{js}*/", padLeft(cleanupNodeJsCode(xdJs.readText()).trim(), "  "))

    val browserJsFile = outputs.files.singleFile
    browserJsFile.outputStream().use {
      it.write(browserJs.toByteArray())
    }
  }
}

fun cleanupNodeJsCode(nodeJsSource: String): String {
  val nodeJsCodeStartIndex = nodeJsSource.indexOf("module.exports")
  return nodeJsSource.substring(0, nodeJsCodeStartIndex)
}

fun padLeft(fileContents: String, padding: String): String {
  val isWindows = System.getProperty("os.name").toLowerCase().contains("windows")
  val newlineChar = if (isWindows) "\r\n" else "\n"

  return fileContents
    .trim()
    .split(newlineChar)
    .joinToString(newlineChar) { if (it.isNotBlank()) "$padding$it" else it }
}
