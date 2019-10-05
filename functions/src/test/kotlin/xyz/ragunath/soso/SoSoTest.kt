package xyz.ragunath.soso

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SoSoTest {
  @Test
  fun `when there are no brackets, then return a depth of 0`() {
    val depth = SoSo.maximumDepthOf("")
    assertThat(depth)
      .isEqualTo(0)
  }

  @Test
  fun `when there is a pair of brackets, then return a depth of 1 (single line)`() {
    val depth = SoSo.maximumDepthOf("{}")
    assertThat(depth)
      .isEqualTo(1)
  }

  @Test
  fun `when there are two pairs of brackets, then return a depth of 2 (single line)`() {
    val depth = SoSo.maximumDepthOf("{{}}")
    assertThat(depth)
      .isEqualTo(2)
  }

  @Test
  fun `when there is a pair of brackets on different lines, then return a depth of 1`() {
    val depth = SoSo.maximumDepthOf(
      """
      {
      }
    """.trimIndent()
    )

    assertThat(depth)
      .isEqualTo(1)
  }

  @Test
  fun `when there is a Kotlin function, then report a depth of 1`() {
    val depth = SoSo.maximumDepthOf(
      """
      fun main() {
      }
    """.trimIndent()
    )
    assertThat(depth)
      .isEqualTo(1)
  }

  @Test
  fun `when there is a Kotlin function with a conditional, then report a depth of 2`() {
    val functionWithConditional = """
      fun main() {
        if (true) {
          // Doesn't matter
        }
      }
    """.trimIndent()

    assertThat(SoSo.maximumDepthOf(functionWithConditional))
      .isEqualTo(2)
  }
}
