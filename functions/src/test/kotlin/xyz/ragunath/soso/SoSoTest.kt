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
}
