package io.redgreen.lang.kotlin

import KotlinLexer
import KotlinParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

fun getFunctions(source: String): List<KtFunction> {
  val lexer = KotlinLexer(CharStreams.fromString(source))
  val parser = KotlinParser(CommonTokenStream(lexer))
  val visitor = KotlinFileVisitor().apply { visit(parser.kotlinFile()) }
  return visitor
    .programElements
    .map { it as KtFunction }
    .toList()
}
