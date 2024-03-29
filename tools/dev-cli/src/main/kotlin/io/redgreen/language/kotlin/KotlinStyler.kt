package io.redgreen.language.kotlin

import KotlinLexer
import KotlinLexer.ABSTRACT
import KotlinLexer.ACTUAL
import KotlinLexer.ANNOTATION
import KotlinLexer.AS
import KotlinLexer.BREAK
import KotlinLexer.BY
import KotlinLexer.BooleanLiteral
import KotlinLexer.CATCH
import KotlinLexer.CLASS
import KotlinLexer.COMMA
import KotlinLexer.COMPANION
import KotlinLexer.CONST
import KotlinLexer.CONTINUE
import KotlinLexer.CROSSINLINE
import KotlinLexer.CharacterLiteral
import KotlinLexer.DATA
import KotlinLexer.DO
import KotlinLexer.DYNAMIC
import KotlinLexer.ELSE
import KotlinLexer.ENUM
import KotlinLexer.EXPECT
import KotlinLexer.EXTERNAL
import KotlinLexer.FINAL
import KotlinLexer.FINALLY
import KotlinLexer.FOR
import KotlinLexer.FUN
import KotlinLexer.GETTER
import KotlinLexer.IF
import KotlinLexer.IMPORT
import KotlinLexer.IN
import KotlinLexer.INFIX
import KotlinLexer.INIT
import KotlinLexer.INLINE
import KotlinLexer.INTERFACE
import KotlinLexer.INTERNAL
import KotlinLexer.IS
import KotlinLexer.IntegerLiteral
import KotlinLexer.LANGLE
import KotlinLexer.LATEINIT
import KotlinLexer.LCURL
import KotlinLexer.LPAREN
import KotlinLexer.LSQUARE
import KotlinLexer.NOINLINE
import KotlinLexer.NOT_IN
import KotlinLexer.NOT_IS
import KotlinLexer.NullLiteral
import KotlinLexer.OBJECT
import KotlinLexer.OPEN
import KotlinLexer.OPERATOR
import KotlinLexer.OUT
import KotlinLexer.OVERRIDE
import KotlinLexer.PACKAGE
import KotlinLexer.PRIVATE
import KotlinLexer.PROTECTED
import KotlinLexer.PUBLIC
import KotlinLexer.QUOTE_CLOSE
import KotlinLexer.QUOTE_OPEN
import KotlinLexer.RANGLE
import KotlinLexer.RCURL
import KotlinLexer.REIFIED
import KotlinLexer.RETURN
import KotlinLexer.RPAREN
import KotlinLexer.RSQUARE
import KotlinLexer.SEALED
import KotlinLexer.SETTER
import KotlinLexer.SUPER
import KotlinLexer.SUSPEND
import KotlinLexer.TAILREC
import KotlinLexer.THIS
import KotlinLexer.THROW
import KotlinLexer.TRIPLE_QUOTE_CLOSE
import KotlinLexer.TRIPLE_QUOTE_OPEN
import KotlinLexer.TRY
import KotlinLexer.TYPE_ALIAS
import KotlinLexer.VAL
import KotlinLexer.VAR
import KotlinLexer.VARARG
import KotlinLexer.WHEN
import KotlinLexer.WHILE
import KotlinParser
import io.redgreen.design.text.LineStyle
import io.redgreen.design.text.StyledText
import io.redgreen.design.text.TextStyle
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.Token
import java.util.Optional

object KotlinStyler {
  private const val DEBUG = false

  private val tokenTypeToCssStyle = mapOf(
    IntegerLiteral to "integer",
    BooleanLiteral to "boolean",
    CharacterLiteral to "char",

    QUOTE_OPEN to "string",
    QUOTE_CLOSE to "end-string",
    TRIPLE_QUOTE_OPEN to "open-multiline-string",
    TRIPLE_QUOTE_CLOSE to "close-multiline-string",

    LPAREN to "parentheses",
    RPAREN to "parentheses",
    LCURL to "curly",
    RCURL to "curly",
    LANGLE to "angled",
    RANGLE to "angled",
    LSQUARE to "squared",
    RSQUARE to "squared",

    COMMA to "comma",
  )

  fun syntaxHighlight(
    outStyledText: StyledText,
    affectedLineNumbers: List<Int>
  ) {
    val charStream = CharStreams.fromString(outStyledText.text)
    val kotlinLexer = KotlinLexer(charStream)
    val commonTokenStream = CommonTokenStream(kotlinLexer).apply { numberOfOnChannelTokens }
    commonTokenStream
      .tokens
      .asSequence()
      .filter { it.line in affectedLineNumbers }
      .onEach { checkAndAddStyle(it, outStyledText) }
      .onEach { highlightKeywords(it, outStyledText) }
      .toList()
  }

  private fun highlightKeywords(token: Token, outStyledText: StyledText) {
    if (isKeyword(token.type)) {
      outStyledText.addStyle(token.toTextStyle("keyword"))
    }
  }

  private fun isKeyword(tokenType: Int): Boolean {
    val keywords = listOf(
      PACKAGE, FUN, RETURN, IMPORT, VAL, THIS, IS, WHEN, ELSE, PRIVATE, CONST, AS, IF, IN,
      SEALED, CLASS, ABSTRACT, DATA, OVERRIDE, NullLiteral, OBJECT, GETTER, VAR, WHILE, DO,
      FOR, INTERFACE, INTERNAL, PRIVATE, PROTECTED, PUBLIC, INLINE, REIFIED, SETTER, SUPER,
      TYPE_ALIAS, BY, COMPANION, INIT, TRY, CATCH, FINALLY, EXPECT, ACTUAL, THROW, CONTINUE,
      BREAK, NOT_IS, NOT_IN, OUT, DYNAMIC, ENUM, ANNOTATION, TAILREC, OPERATOR, INFIX, EXTERNAL,
      SUSPEND, FINAL, OPEN, LATEINIT, VARARG, NOINLINE, CROSSINLINE
    )
    return tokenType in keywords
  }

  fun addLanguageSemantics(outStyledText: StyledText) {
    val lexer = KotlinLexer(CharStreams.fromString(outStyledText.text))
    val parser = KotlinParser(CommonTokenStream(lexer))
    if (!DEBUG) {
      parser.removeErrorListeners()
    }
    val visitor = KotlinLanguageElementVisitor().apply { visit(parser.kotlinFile()) }
    visitor.functions.onEach { function -> addFunctionDelimiters(outStyledText, function) }
    markFunctionParameterDeclarations(outStyledText, visitor.functions)

    visitor.identifiers.onEach { identifier ->
      val startIndex = identifier.startIndex
      val endIndex = identifier.startIndex + identifier.text.length - 1
      val textRange = startIndex..endIndex

      outStyledText.addStyle(TextStyle("identifier", identifier.lineNumber, textRange, Optional.of(identifier.text)))
    }
  }

  private fun markFunctionParameterDeclarations(
    outStyledText: StyledText,
    functions: List<Function>
  ) {
    functions
      .map(Function::signature)
      .flatMap(Signature::parameters)
      .map(Parameter::identifier)
      .onEach { identifier -> outStyledText.addFunctionParameterStyles(identifier) }
  }

  private fun StyledText.addFunctionParameterStyles(identifier: Identifier) {
    val startIndex = identifier.startIndex
    val endIndex = identifier.startIndex + identifier.text.length - 1
    val textRange = startIndex..endIndex

    addStyle(TextStyle("identifier", identifier.lineNumber, textRange, Optional.of(identifier.text)))
    addStyle(TextStyle("declaration", identifier.lineNumber, textRange))
  }

  private fun addFunctionDelimiters(
    outStyledText: StyledText,
    function: Function
  ) {
    with(outStyledText) {
      addStyle(LineStyle("begin-function", function.startLine))
      addStyle(LineStyle("end-function", function.endLine))
    }
  }

  private fun checkAndAddStyle(token: Token, outStyledText: StyledText) {
    val cssClassName = tokenTypeToCssStyle[token.type]
    cssClassName?.let { outStyledText.addStyle(token.toTextStyle(cssClassName)) }
  }

  private fun Token.toTextStyle(name: String): TextStyle {
    val lineNumber = this.line
    val startIndex = this.charPositionInLine
    val endIndex = startIndex + this.text.length - 1
    return TextStyle(name, lineNumber, startIndex..endIndex)
  }
}
