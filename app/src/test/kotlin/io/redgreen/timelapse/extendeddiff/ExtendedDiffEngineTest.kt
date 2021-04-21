package io.redgreen.timelapse.extendeddiff

import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.extendeddiff.ExtendedDiff.NoChanges
import org.junit.jupiter.api.Test

class ExtendedDiffEngineTest {
  @Test
  internal fun `it should create a new instance of the engine for every new text`() {
    // given & when
    val diffEngine = ExtendedDiffEngine.newInstance("Hello, world!")

    // then
    assertThat(diffEngine)
      .isInstanceOf(ExtendedDiffEngine::class.java)
  }

  @Test
  fun `it should return the seed text as the extended diff`() {
    // given
    val diffEngine = ExtendedDiffEngine.newInstance("Hello, world!")

    // when
    val extendedDiff = diffEngine.extendedDiff("")

    // then
    assertThat(extendedDiff)
      .isEqualTo(NoChanges("Hello, world!"))
  }
}
