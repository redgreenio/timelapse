package io.redgreen.timelapse.extendeddiff

import com.google.common.truth.Truth.assertThat
import io.redgreen.scout.ParseResult
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Added
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Deleted
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Modified
import io.redgreen.timelapse.extendeddiff.ExtendedDiff.HasChanges
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

  @Test
  fun `it should apply a patch and return an extended diff`() {
    // given
    val seedText = "fun a() {}"
    val patch = """
      --- a.txt	2021-04-21 12:49:53.000000000 +0530
      +++ b.txt	2021-04-21 12:50:04.000000000 +0530
      @@ -1 +1,2 @@
      -fun a() {}
      \ No newline at end of file
      +fun a() {}
      +fun b() {}
      \ No newline at end of file
    """.trimIndent()

    val diffEngine = ExtendedDiffEngine.newInstance(seedText)

    // when
    val extendedDiff = diffEngine.extendedDiff(patch)

    // then
    val patchedText = """
      fun a() {}
      fun b() {}
    """.trimIndent()
    val comparisonResults = listOf(
      Added(ParseResult.wellFormedFunction("b", 2, 2, 1))
    )

    assertThat(extendedDiff)
      .isEqualTo(HasChanges(patchedText, comparisonResults))
  }

  @Test
  fun `it should apply successive patches and return an extended diff`() {
    // given
    val seedText = "fun a() {}"

    val patch1 = """
      --- a.txt	2021-04-21 12:49:53.000000000 +0530
      +++ b.txt	2021-04-21 12:50:04.000000000 +0530
      @@ -1 +1,2 @@
      -fun a() {}
      \ No newline at end of file
      +fun a() {}
      +fun b() {}
      \ No newline at end of file
    """.trimIndent()

    val patch2 = """
      --- a.txt	2021-04-21 13:25:55.000000000 +0530
      +++ b.txt	2021-04-21 13:26:00.000000000 +0530
      @@ -1,2 +1 @@
      -fun a() {}
       fun b() {}
      \ No newline at end of file
    """.trimIndent()

    val patches = listOf(patch1, patch2)
    val diffEngine = ExtendedDiffEngine.newInstance(seedText)

    // when
    diffEngine.extendedDiff(patch1)
    val extendedDiff = diffEngine.extendedDiff(patch2)

    // then
    val patchedText = """
      fun b() {}
    """.trimIndent()
    val comparisonResults = listOf(
      Deleted(ParseResult.wellFormedFunction("a", 1, 1, 1)),
      Modified(ParseResult.wellFormedFunction("b", 1, 1, 1))
    )

    assertThat(extendedDiff)
      .isEqualTo(HasChanges(patchedText, comparisonResults))
  }
}
