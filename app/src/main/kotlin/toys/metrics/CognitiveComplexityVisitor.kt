package toys.metrics

import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

class CognitiveComplexityVisitor : KtTreeVisitorVoid() {
  internal var complexity = 0

  override fun visitKtFile(file: KtFile) {
    complexity = CognitiveComplexity.calculate(file)
  }
}
