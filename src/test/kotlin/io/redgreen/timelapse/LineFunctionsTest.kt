package io.redgreen.timelapse

import arrow.core.Tuple5
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class LineFunctionsTest {
  @Nested
  @TestInstance(PER_CLASS)
  inner class HorizontalLine {
    @ParameterizedTest
    @MethodSource("horizontalLineCoordinates")
    fun `it should calculate slope for horizontal lines`(
      coordinates: Tuple5<Int, Int, Int, Int, Double>
    ) {
      // given
      val (x1, y1, x2, y2, expectedSlope) = coordinates

      // when
      val slope = calculateSlope(x1, y1, x2, y2)

      // then
      assertThat(slope)
        .isEqualTo(expectedSlope)
    }

    @Suppress("unused") // function is referenced in @MethodSource
    fun horizontalLineCoordinates(): List<Tuple5<Int, Int, Int, Int, Double>> {
      return listOf(
        // Lines passing through origin (left -> right)
        Tuple5(0, 0, 1, 0, 0.0),
        Tuple5(-1, 0, 0, 0, 0.0),
        Tuple5(-1, 0, 1, 0, 0.0),

        // Lines passing through origin (right -> left)
        Tuple5(0, 0, -1, 0, -0.0),
        Tuple5(1, 0, 0, 0, -0.0),
        Tuple5(1, 0, -1, 0, -0.0),

        // Lines away from origin (left -> right)
        Tuple5(1, 4, 2, 4, 0.0),
        Tuple5(-1, 4, 1, 4, 0.0),
        Tuple5(-2, 4, -1, 4, 0.0),

        // Lines away from origin (right -> left)
        Tuple5(2, -4, 1, -4, -0.0),
        Tuple5(1, -4, -1, -4, -0.0),
        Tuple5(-1, -4, -2, -4, -0.0),
      )
    }
  }
}
