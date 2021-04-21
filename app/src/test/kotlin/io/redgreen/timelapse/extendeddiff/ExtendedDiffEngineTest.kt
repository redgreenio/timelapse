package io.redgreen.timelapse.extendeddiff

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class ExtendedDiffEngineTest {
  @Test
  internal fun `it should create a new instance of the engine for every new text`() {
    val engine = ExtendedDiffEngine.newInstance("Hello, world!")

    assertThat(engine)
      .isInstanceOf(ExtendedDiffEngine::class.java)
  }
}
