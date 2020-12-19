package io.redgreen.timelapse.search

import arrow.core.Tuple3
import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.search.Occurrence.Segment
import java.util.stream.Stream
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.provider.ValueSource

class FilePathIndexTest {
  private val filePaths = listOf(
    "io/redgreen/timelapse/diff/Patch.kt",
    "io/redgreen/timelapse/diff/FormattedDiff.kt",
    "io/redgreen/timelapse/diff/DiffLine.kt",
    "io/redgreen/timelapse/diff/DiffViewer.kt",
    "io/redgreen/timelapse/diff/PatchViewer.kt",
  )

  private val filePathIndex = FilePathIndex.from(filePaths)

  @ParameterizedTest
  @ValueSource(strings = ["  ", "", "\t"])
  fun `it should return all files for empty or blank search strings`(term: String) {
    // when
    val matches = filePathIndex.search(term)

    // then
    assertThat(matches)
      .containsExactly(*filePaths.map { Match(it) }.toTypedArray())
      .inOrder()
  }

  @Test
  fun `it should return a match when there's a complete match`() {
    // given
    val term = "io/redgreen/timelapse/diff/Patch.kt"

    // when
    val matches = filePathIndex.search(term)

    // then
    assertThat(matches)
      .containsExactly(
        Match("io/redgreen/timelapse/diff/Patch.kt", listOf(Segment(0, 35)))
      )
  }

  @ParameterizedTest
  @ArgumentsSource(PartialMatchArgumentsProvider::class)
  fun `it should return partial matches from the file path`(termAndSegment: Tuple3<String, Int, Int>) {
    // given
    val (term, startIndex, chars) = termAndSegment

    // when
    val matches = filePathIndex.search(term)

    // then
    assertThat(matches)
      .containsExactly(*filePaths.map { Match(it, listOf(Segment(startIndex, chars))) }.toTypedArray())
      .inOrder()
  }

  class PartialMatchArgumentsProvider : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
      return listOf(
        Tuple3("io", 0, 2),
        Tuple3("redgreen", 3, 8),
        Tuple3("timelapse", 12, 9),
        Tuple3("diff", 22, 4),
        Tuple3("pse/diff", 18, 8),
      )
        .map { Arguments.of(it) }
        .stream()
    }
  }

  @Test
  fun `it should match file extensions`() {
    // given
    val extension = ".kt"

    // when
    val matches = filePathIndex.search(extension)


    // then
    val expectedSegments = listOf(
      Segment(32, 3),
      Segment(40, 3),
      Segment(35, 3),
      Segment(37, 3),
      Segment(38, 3),
    )
    val filePathsWithSegments = filePaths.zip(expectedSegments) { filePath, segment -> Match(filePath, listOf(segment)) }

    assertThat(matches)
      .containsExactly(*filePathsWithSegments.toTypedArray())
      .inOrder()
  }

  @ParameterizedTest
  @ValueSource(strings = ["patch", "PATCH"])
  fun `it should ignore case (all small) in search term during searching`(term: String) {
    // when
    val matches = filePathIndex.search(term)

    // then
    assertThat(matches)
      .containsExactly(
        Match("io/redgreen/timelapse/diff/Patch.kt", listOf(Segment(27, 5))),
        Match("io/redgreen/timelapse/diff/PatchViewer.kt", listOf(Segment(27, 5)))
      )
      .inOrder()
  }
}
