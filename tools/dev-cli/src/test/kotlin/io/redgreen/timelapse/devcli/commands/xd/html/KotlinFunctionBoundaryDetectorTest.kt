package io.redgreen.timelapse.devcli.commands.xd.html

import KotlinLexer
import KotlinParser
import com.google.common.truth.Truth.assertThat
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.jupiter.api.Test

class KotlinFunctionBoundaryDetectorTest {
  @Test
  fun `it should detect function start and end line number for a single top-level function`() {
    // given
    val singleTopLevelFunction = """
      fun isThisFun(): Boolean {
        return true
      }
    """.trimIndent()

    val lexer = KotlinLexer(CharStreams.fromString(singleTopLevelFunction))
    val parser = KotlinParser(CommonTokenStream(lexer))
    val visitor = KotlinLanguageElementVisitor()

    // when
    visitor.visit(parser.kotlinFile())

    // then
    assertThat(visitor.functions)
      .containsExactly(Function(1, 3, "isThisFun"))
  }
}
