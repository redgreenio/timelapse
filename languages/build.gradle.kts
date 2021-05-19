plugins {
  id(pluginsDeps.antlr.id)
}

dependencies {
  implementation(deps.log4j.api)
  implementation(deps.log4j.core)

  antlr(deps.antlr.core)
  api(deps.antlr.runtime)

  testImplementation(deps.test.junit.api)
  testRuntimeOnly(deps.test.junit.engine)

  testImplementation(deps.test.truth)
}

tasks.generateGrammarSource {
  maxHeapSize = "64m"
  arguments = arguments + listOf("-visitor", "-long-messages")
}

tasks
  .findByName("compileKotlin")
  ?.dependsOn(tasks.findByName("generateGrammarSource"))
