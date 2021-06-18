package toys.metrics

import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

internal class ClocCountVisitor : KtTreeVisitorVoid() {
  internal var count = 0

  private fun increment(value: Int) {
    count += value
  }

  override fun visitComment(comment: PsiComment) {
    increment(comment.text.split('\n').size)
  }

  override fun visitDeclaration(dcl: KtDeclaration) {
    val text = dcl.docComment?.text
    if (text != null) {
      increment(text.split('\n').size)
    }
    super.visitDeclaration(dcl)
  }
}
