package io.redgreen.timelapse.devcli.commands.xd.html

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
import io.redgreen.design.text.StyledText
import io.redgreen.design.text.TextStyle
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.Token

object KotlinSyntaxHighlighter {
  fun addStylesForTokens(
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
      .onEach { highlightStringLiterals(it, outStyledText) }
      .onEach { highlightBrackets(it, outStyledText) }
      .onEach { highlightLiterals(it, outStyledText) }
      .onEach { highlightKeywords(it, outStyledText) }
      .onEach { highlightCommas(it, outStyledText) }
      .toList()
  }

  private fun highlightCommas(token: Token, outStyledText: StyledText) {
    if (token.type == COMMA) {
      outStyledText.addStyle(TextStyle("comma", token.line, token.charPositionInLine))
    }
  }

  private fun highlightLiterals(token: Token, outStyledText: StyledText) {
    when (token.type) {
      IntegerLiteral -> {
        outStyledText.addStyle(TextStyle("integer", token.line, token.charPositionInLine))
      }
      BooleanLiteral -> {
        val startIndex = token.charPositionInLine
        val stopIndex = startIndex + token.text.length - 1
        outStyledText.addStyle(TextStyle("boolean", token.line, startIndex..stopIndex))
      }
      CharacterLiteral -> {
        val startIndex = token.charPositionInLine
        val stopIndex = startIndex + token.text.length - 1
        outStyledText.addStyle(TextStyle("char", token.line, startIndex..stopIndex))
      }
    }
  }

  private fun highlightStringLiterals(
    token: Token,
    outStyledText: StyledText
  ) {
    when (token.type) {
      QUOTE_OPEN -> {
        outStyledText.addStyle(TextStyle("string", token.line, token.charPositionInLine))
      }
      QUOTE_CLOSE -> {
        outStyledText.addStyle(TextStyle("end-string", token.line, token.charPositionInLine))
      }
      TRIPLE_QUOTE_OPEN -> {
        val startIndex = token.charPositionInLine
        val stopIndex = startIndex + token.text.length - 1
        outStyledText.addStyle(TextStyle("open-multiline-string", token.line, startIndex..stopIndex))
      }
      TRIPLE_QUOTE_CLOSE -> {
        val startIndex = token.charPositionInLine
        val stopIndex = startIndex + token.text.length - 1
        outStyledText.addStyle(TextStyle("close-multiline-string", token.line, startIndex..stopIndex))
      }
    }
  }

  private fun highlightKeywords(token: Token, outStyledText: StyledText) {
    if (isKeyword(token.type)) {
      val startIndex = token.charPositionInLine
      val stopIndex = startIndex + token.text.length - 1
      outStyledText.addStyle(TextStyle("keyword", token.line, startIndex..stopIndex))
    }
  }

  private fun highlightBrackets(token: Token, outStyledText: StyledText) {
    if (token.type == LPAREN || token.type == RPAREN) {
      outStyledText.addStyle(TextStyle("parentheses", token.line, token.charPositionInLine))
    } else if (token.type == LCURL || token.type == RCURL) {
      outStyledText.addStyle(TextStyle("curly", token.line, token.charPositionInLine))
    } else if (token.type == LANGLE || token.type == RANGLE) {
      outStyledText.addStyle(TextStyle("angled", token.line, token.charPositionInLine))
    } else if (token.type == LSQUARE || token.type == RSQUARE) {
      outStyledText.addStyle(TextStyle("squared", token.line, token.charPositionInLine))
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
}
