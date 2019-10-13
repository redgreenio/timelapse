package xyz.ragunath.soso

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class FunctionFinderAnalyzerIntegrationTest {
  @Test
  fun `it can detect top-level functions from a file`() {
    val functionWithTopLevelFunctions = """
      fun add(a: Int, b: Int) {
        return a + b
      }

      fun subtract(a: Int, b: Int) {
        return a - b
      }

      fun multiply(a: Int, b: Int) {
        return a * b
      }
    """.trimIndent()

    assertThat(detectFunctions(functionWithTopLevelFunctions))
      .hasSize(3)

    assertThat(detectFunctions(functionWithTopLevelFunctions))
      .containsExactly(
        Result.with(1, 3, 1),
        Result.with(5, 7, 1),
        Result.with(9, 11, 1)
      )
      .inOrder()
  }
}
