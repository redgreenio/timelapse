plugins {
  id("antlr")
}

dependencies {
  implementation(deps.log4j.api)
  implementation(deps.log4j.core)

  antlr("org.antlr:antlr4:4.9.2")
  implementation("org.antlr:antlr4-runtime:4.9.2")

  testImplementation(deps.test.junit.api)
  testRuntimeOnly(deps.test.junit.engine)

  testImplementation(deps.test.truth)
}

tasks.generateGrammarSource {
  maxHeapSize = "64m"
  arguments = arguments + listOf("-visitor", "-long-messages")
}
