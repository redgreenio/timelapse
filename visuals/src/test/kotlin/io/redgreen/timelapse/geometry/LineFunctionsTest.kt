package io.redgreen.timelapse.geometry

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
    ArgumentsSource(FortyFiveDegreeIncrementsArgumentsProvider::class),
    ArgumentsSource(RandomLinesArgumentsProvider::class),
  )
  fun `it should calculate slope`(
    coordinates: Tuple5<Double, Int, Double, Int, Double>
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

class FortyFiveDegreeIncrementsArgumentsProvider : ArgumentsProvider {
  override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> {
    return listOf(
      // 45 degrees (left -> right)
      Tuple5(-1, -1, 1, 1, 1.0),
      Tuple5(0, -3, 3, 0, 1.0),

      // 45 degrees (right -> left)
      Tuple5(1, 1, -1, -1, 1.0),
      Tuple5(3, 0, 0, -3, 1.0),

      // 135 degrees (left -> right)
      Tuple5(-1, 1, 1, -1, -1.0),
      Tuple5(0, 3, 3, 0, -1.0),

      // 135 degrees (right -> left)
      Tuple5(1, -1, -1, 1, -1.0),
      Tuple5(3, 0, 0, 3, -1.0),
    ).toArgumentsStream()
  }
}

class RandomLinesArgumentsProvider : ArgumentsProvider {
  override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> {
    return listOf(
      Tuple5(2, 8, 3, 20, 12.0),
      Tuple5(-2, 1, -1, 5, 4.0),
      Tuple5(-1, -2, 3, -1, 0.25),
      Tuple5(-3, 1, 3, 4, 0.5),
      Tuple5(0, 4, 2, 1, -1.5),
      Tuple5(-2, 6, 0, 2, -2.0),
    ).toArgumentsStream()
  }
}

private fun <T> List<T>.toArgumentsStream(): Stream<out Arguments> =
  this.map { Arguments.of(it) }.stream() // TODO: 19/12/20 Extract into a separate test fixtures module
