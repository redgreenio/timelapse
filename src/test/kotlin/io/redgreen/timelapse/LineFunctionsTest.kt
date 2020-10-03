package io.redgreen.timelapse

import arrow.core.Tuple5
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.provider.ArgumentsSources
import java.util.stream.Stream
import kotlin.Double.Companion.NEGATIVE_INFINITY
import kotlin.Double.Companion.POSITIVE_INFINITY

class LineFunctionsTest {
  @ParameterizedTest
  @ArgumentsSources(
    ArgumentsSource(HorizontalLineArgumentsProvider::class),
    ArgumentsSource(VerticalLineArgumentsProvider::class),
  )
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
    return listOf(
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
    ).toArgumentsStream()
  }
}

class VerticalLineArgumentsProvider : ArgumentsProvider {
  override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> {
    return listOf(
      // Lines passing through origin (top -> bottom)
      Tuple5(0, 1, 0, 0, NEGATIVE_INFINITY),
      Tuple5(0, 0, 0, -1, NEGATIVE_INFINITY),
      Tuple5(0, 1, 0, -1, NEGATIVE_INFINITY),

      // Lines passing through origin (bottom -> top)
      Tuple5(0, 0, 0, 1, POSITIVE_INFINITY),
      Tuple5(0, -1, 0, 0, POSITIVE_INFINITY),
      Tuple5(0, -1, 0, 1, POSITIVE_INFINITY),

      // Lines parallel to y-axis (top -> bottom)
      Tuple5(2, 4, 2, 2, NEGATIVE_INFINITY),
      Tuple5(2, 1, 2, -1, NEGATIVE_INFINITY),
      Tuple5(-2, -1, -2, -2, NEGATIVE_INFINITY),

      // Lines parallel to y-axis (bottom -> top)
      Tuple5(2, 2, 2, 4, POSITIVE_INFINITY),
      Tuple5(2, -1, 2, 1, POSITIVE_INFINITY),
      Tuple5(-2, -2, -2, -1, POSITIVE_INFINITY),
    ).toArgumentsStream()
  }
}

private fun <T> List<T>.toArgumentsStream(): Stream<out Arguments> =
  this.map { Arguments.of(it) }.stream()
