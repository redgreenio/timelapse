import listParser.ElemContext
import listParser.ElemsContext
import listParser.ListContext
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

fun main() {
  val lexer = listLexer(CharStreams.fromString("[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]"))
  val parser = listParser(CommonTokenStream(lexer))
  val result = SumVisitor().visit(parser.list())
  println(result)
}

class SumVisitor : listBaseVisitor<Int>() {
  override fun visitList(context: ListContext): Int {
    context.elems() ?: return 0
    return visitElems(context.elems())
  }

  override fun visitElems(context: ElemsContext): Int {
    var sum = 0
    for (elemContext in context.elem()) {
      sum += this.visitElem(elemContext)
    }
    return sum
  }

  override fun visitElem(context: ElemContext): Int {
    return Integer.parseInt(context.text)
  }
}
