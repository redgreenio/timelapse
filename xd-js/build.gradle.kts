import com.github.gradle.node.npm.task.NpmTask

plugins {
  id("com.github.node-gradle.node") version "3.1.0"
}

node {
  download.set(true)
  version.set("14.17.0")
}

tasks.register("testJs", NpmTask::class) {
  dependsOn(tasks.findByName("npmInstall"))
  args.set(listOf("test"))
}
