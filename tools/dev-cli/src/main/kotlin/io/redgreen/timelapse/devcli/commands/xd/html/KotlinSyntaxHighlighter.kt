package io.redgreen.timelapse.devcli.commands.xd.html

import KotlinLexer
import KotlinLexer.ABSTRACT
import KotlinLexer.AS
import KotlinLexer.CLASS
import KotlinLexer.CONST
import KotlinLexer.DATA
import KotlinLexer.ELSE
import KotlinLexer.FUN
import KotlinLexer.GETTER
import KotlinLexer.IF
import KotlinLexer.IMPORT
import KotlinLexer.IN
import KotlinLexer.IS
import KotlinLexer.NullLiteral
import KotlinLexer.OBJECT
import KotlinLexer.OVERRIDE
import KotlinLexer.PACKAGE
import KotlinLexer.PRIVATE
import KotlinLexer.QUOTE_CLOSE
import KotlinLexer.QUOTE_OPEN
import KotlinLexer.RETURN
import KotlinLexer.SEALED
import KotlinLexer.THIS
import KotlinLexer.VAL
import KotlinLexer.WHEN
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
      .filter { it.line in affectedLineNumbers }
      .onEach { token ->
        if (token.type == QUOTE_OPEN) {
          outStyledText.addStyle(TextStyle("string", token.line, token.charPositionInLine))
        } else if (token.type == QUOTE_CLOSE) {
          outStyledText.addStyle(TextStyle("end_string", token.line, token.charPositionInLine))
        }
      }
      .onEach<Token, List<Token>> {
        if (isKeyword(it.type)) {
          val startIndex = it.charPositionInLine
          val stopIndex = startIndex + it.text.length - 1
          outStyledText.addStyle(TextStyle("keyword", it.line, startIndex..stopIndex))
        }
      }
  }

  private fun isKeyword(tokenType: Int): Boolean {
    val keywords = listOf(
      PACKAGE, FUN, RETURN, IMPORT, VAL, THIS, IS, WHEN, ELSE, PRIVATE, CONST, AS, IF, IN,
      SEALED, CLASS, ABSTRACT, DATA, OVERRIDE, NullLiteral, OBJECT, GETTER
    )
    return tokenType in keywords
  }
}
