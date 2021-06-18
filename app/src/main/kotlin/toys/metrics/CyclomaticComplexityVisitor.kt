package toys.metrics

import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

class CyclomaticComplexityVisitor : KtTreeVisitorVoid() {
  internal var complexity = 0

  override fun visitKtFile(file: KtFile) {
    complexity = CyclomaticComplexity.calculate(file) {
      ignoreSimpleWhenEntries = false
    }
  }
}
