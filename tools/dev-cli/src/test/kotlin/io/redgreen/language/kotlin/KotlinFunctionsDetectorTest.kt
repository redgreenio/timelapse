package io.redgreen.language.kotlin

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
      val isThisFunSignature = Signature(Identifier("isThisFun", 1, 4))
      assertThat(visitor.functions)
        .containsExactly(Function(1, 3, isThisFunSignature))
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
      val addSignature = Signature(
        Identifier("add", 3, 4),
        Parameter(Identifier("a", 3, 8)),
        Parameter(Identifier("b", 3, 16))
      )

      val multiplySignature = Signature(
        Identifier("multiply", 5, 4),
        Parameter(Identifier("a", 5, 13)),
        Parameter(Identifier("b", 5, 21))
      )

      assertThat(visitor.functions)
        .containsExactly(
          Function(3, 3, addSignature),
          Function(5, 7, multiplySignature),
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
      val addSignature = Signature(
        Identifier("add", 4, 4),
        Parameter(Identifier("a", 4, 8)),
        Parameter(Identifier("b", 4, 16)),
      )
      assertThat(visitor.functions)
        .containsExactly(Function(3, 4, addSignature))
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
      val addSignature = Signature(
        Identifier("add", 3, 4),
        Parameter(Identifier("a", 3, 8)),
        Parameter(Identifier("b", 3, 16))
      )
      val multiplySignature = Signature(
        Identifier("multiply", 3, 37),
        Parameter(Identifier("a", 3, 46)),
        Parameter(Identifier("b", 3, 54))
      )

      assertThat(visitor.functions)
        .containsExactly(
          Function(3, 3, addSignature),
          Function(3, 3, multiplySignature)
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
