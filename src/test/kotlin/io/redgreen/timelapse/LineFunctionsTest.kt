package io.redgreen.timelapse

import arrow.core.Tuple5
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.stream.Stream

class LineFunctionsTest {
  @ParameterizedTest
  @ArgumentsSource(HorizontalLineArgumentsProvider::class)
  fun `it should calculate slope`(
    coordinates: Tuple5<Int, Int, Int, Int, Double>
  ) {
    // given
    val (x1, y1, x2, y2, expectedSlope) = coordinates

    // when
    val actualSlope = calculateSlope(x1, y1, x2, y2)

    // then
    assertThat(actualSlope)
      .isEqualTo(expectedSlope)
  }
}

class HorizontalLineArgumentsProvider : ArgumentsProvider {
  override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
    val coordinates = listOf(
      // Lines passing through origin (left -> right)
      Tuple5(0, 0, 1, 0, 0.0),
      Tuple5(-1, 0, 0, 0, 0.0),
      Tuple5(-1, 0, 1, 0, 0.0),

      // Lines passing through origin (right -> left)
      Tuple5(0, 0, -1, 0, -0.0),
      Tuple5(1, 0, 0, 0, -0.0),
      Tuple5(1, 0, -1, 0, -0.0),

      // Lines parallel to x-axis (left -> right)
      Tuple5(1, 4, 2, 4, 0.0),
      Tuple5(-1, 4, 1, 4, 0.0),
      Tuple5(-2, 4, -1, 4, 0.0),

      // Lines parallel to x-axis (right -> left)
      Tuple5(2, -4, 1, -4, -0.0),
      Tuple5(1, -4, -1, -4, -0.0),
      Tuple5(-1, -4, -2, -4, -0.0),
    )

    return coordinates.map { Arguments.of(it) }.stream()
  }
}
