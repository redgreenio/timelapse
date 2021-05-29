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
      val isThisFunIdentifier = Identifier("isThisFun", 1, 4)
      assertThat(visitor.functions)
        .containsExactly(Function(1, 3, Signature(isThisFunIdentifier)))
    }

    @Test
    fun `function with parameters and return type`() {
      // given
      val twoFunctions = """
        package io.redgreen.math
        
        fun add(a: Int, b: Int) = a + b
        
        fun multiply(a: Int, b: Int): Int {
          return a * b
        }
      """.trimIndent()

      // when
      val visitor = visitedVisitor(twoFunctions)

      // then
      val addIdentifier = Identifier("add", 3, 4)
      val multiplyIdentifier = Identifier("multiply", 5, 4)
      assertThat(visitor.functions)
        .containsExactly(
          Function(3, 3, Signature(addIdentifier)),
          Function(5, 7, Signature(multiplyIdentifier)),
        )
    }

    @Test
    fun `annotated function`() {
      // given
      val twoFunctions = """
        package io.redgreen.math
        
        @Redundant
        fun add(a: Int, b: Int) = a + b
      """.trimIndent()

      // when
      val visitor = visitedVisitor(twoFunctions)

      // then
      val addIdentifier = Identifier("add", 4, 4)
      assertThat(visitor.functions)
        .containsExactly(Function(3, 4, Signature(addIdentifier)))
    }

    @Test
    fun `two function, same line`() {
      // given
      val twoFunctions = """
        package io.redgreen.math
        
        fun add(a: Int, b: Int) = a + b; fun multiply(a: Int, b: Int) = a * b
      """.trimIndent()

      // when
      val visitor = visitedVisitor(twoFunctions)

      // then
      val addIdentifier = Identifier("add", 3, 4)
      val multiplyIdentifier = Identifier("multiply", 3, 37)
      assertThat(visitor.functions)
        .containsExactly(
          Function(3, 3, Signature(addIdentifier)),
          Function(3, 3, Signature(multiplyIdentifier))
        )
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
