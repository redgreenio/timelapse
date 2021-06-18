package toys.metrics

import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

class SlocVisitor : KtTreeVisitorVoid() {
  internal var count = 0

  override fun visitKtFile(file: KtFile) {
    val lines = file.text.split('\n')
    count = Sloc().count(lines)
  }

  private class Sloc {
    private val comments = arrayOf("//", "/*", "*/", "*")

    fun count(lines: List<String>): Int {
      return lines
        .map { it.trim() }
        .filter { trim -> trim.isNotEmpty() && !comments.any { trim.startsWith(it) } }
        .size
    }
  }
}
