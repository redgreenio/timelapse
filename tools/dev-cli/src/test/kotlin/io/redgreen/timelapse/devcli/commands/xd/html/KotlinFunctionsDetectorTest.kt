package io.redgreen.timelapse.devcli.commands.xd.html

import KotlinLexer
import KotlinParser
import com.google.common.truth.Truth.assertThat
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class KotlinFunctionsDetectorTest {
  @Nested
  inner class TopLevel {
    @Test
    fun `single function no parameters or return type`() {
      // given
      val singleTopLevelFunction = """
        fun isThisFun(): Boolean {
          return true
        }
      """.trimIndent()

      // when
      val visitor = visitedVisitor(singleTopLevelFunction)

      // then
      assertThat(visitor.functions)
        .containsExactly(Function(1, 3, "isThisFun"))
    }
  }

  private fun visitedVisitor(
    source: String
  ): KotlinLanguageElementVisitor {
    val lexer = KotlinLexer(CharStreams.fromString(source))
    val parser = KotlinParser(CommonTokenStream(lexer))
    return KotlinLanguageElementVisitor().apply { visit(parser.kotlinFile()) }
  }
}
