package toys.metrics

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.psi.KtPsiFactory

fun main() {
  val coreEnvironment = createKtCoreEnvironment()
  val psiFactory = KtPsiFactory(coreEnvironment.project, false)
  val ktFile = psiFactory.createFile(
    "X.kt",
    ClocCountVisitor::class.java.classLoader.getResourceAsStream("toys/complexity-kt.txt")!!.reader().readText()
  )

  val cognitiveComplexityVisitor = CognitiveComplexityVisitor()
  ktFile.accept(cognitiveComplexityVisitor)
  println("Cognitive complexity: ${cognitiveComplexityVisitor.complexity}")

  val cyclomaticComplexityVisitor = CyclomaticComplexityVisitor()
  ktFile.accept(cyclomaticComplexityVisitor)
  println("Cyclomatic complexity: ${cyclomaticComplexityVisitor.complexity}")
}

private fun createKtCoreEnvironment(
  configuration: CompilerConfiguration = CompilerConfiguration(),
  disposable: Disposable = Disposer.newDisposable()
): KotlinCoreEnvironment {
  // https://github.com/JetBrains/kotlin/commit/2568804eaa2c8f6b10b735777218c81af62919c1
  setIdeaIoUseFallback()
  configuration.put(
    CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
    PrintingMessageCollector(System.err, MessageRenderer.PLAIN_FULL_PATHS, false)
  )
  configuration.put(CommonConfigurationKeys.MODULE_NAME, "skrawberry")

  return KotlinCoreEnvironment.createForProduction(
    disposable,
    configuration,
    EnvironmentConfigFiles.JVM_CONFIG_FILES
  )
}
