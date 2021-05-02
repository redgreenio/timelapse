import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

fun main() {
  val lexer = listLexer(CharStreams.fromString("[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]"))
  val parser = listParser(CommonTokenStream(lexer))
  val result = SumListVisitor().visit(parser.list())
  println(result)
}
