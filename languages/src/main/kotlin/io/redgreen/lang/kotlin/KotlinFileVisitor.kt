package io.redgreen.lang.kotlin

import KotlinParser.FunctionDeclarationContext
import KotlinParserBaseVisitor
import io.redgreen.lang.ProgramElement

class KotlinFileVisitor : KotlinParserBaseVisitor<ProgramElement>() {
  val programElements: MutableList<ProgramElement> = mutableListOf()

  override fun visitFunctionDeclaration(context: FunctionDeclarationContext): ProgramElement {
    val identifier = context.simpleIdentifier().text
    val receiverType = context.receiverType()?.text
    val startLine = context.start.line
    val functionName = if (receiverType == null) {
      identifier
    } else {
      "$receiverType.$identifier"
    }
    val ktFunction = KtFunction(functionName, startLine, context.stop.line)
    programElements.add(ktFunction)
    return ktFunction
  }
}
