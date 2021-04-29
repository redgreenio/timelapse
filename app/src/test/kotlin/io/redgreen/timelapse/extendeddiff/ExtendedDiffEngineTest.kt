package io.redgreen.timelapse.extendeddiff

import com.google.common.truth.Truth.assertThat
import io.redgreen.scout.ParseResult
import io.redgreen.scout.languages.kotlin.KotlinFunctionScanner
import io.redgreen.scout.languages.swift.SwiftFunctionScanner
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Added
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Deleted
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Modified
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Unmodified
import io.redgreen.timelapse.extendeddiff.ExtendedDiff.HasChanges
import io.redgreen.timelapse.extendeddiff.ExtendedDiff.NoChanges
import io.redgreen.timelapse.foo.readResourceText
import org.approvaltests.Approvals
import org.approvaltests.reporters.QuietReporter
import org.approvaltests.reporters.UseReporter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.OS

class ExtendedDiffEngineTest {
  @Test
  fun `it should create a new instance of the engine for every new text`() {
    // given & when
    val diffEngine = ExtendedDiffEngine.newInstance("/* Hello, world! */", KotlinFunctionScanner)

    // then
    assertThat(diffEngine)
      .isInstanceOf(ExtendedDiffEngine::class.java)
  }

  @Test
  fun `it should return the seed text as the extended diff`() {
    // given
    val diffEngine = ExtendedDiffEngine.newInstance("/* Hello, world! */", KotlinFunctionScanner)

    // when
    val extendedDiff = diffEngine.extendedDiff("")

    // then
    assertThat(extendedDiff)
      .isEqualTo(NoChanges("/* Hello, world! */"))
  }

  @Test
  @DisabledOnOs(OS.WINDOWS)
  fun `it should apply a patch and return an extended diff`() {
    // given
    val kotlinSourceCode = "fun a() {}"
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

    val diffEngine = ExtendedDiffEngine.newInstance(kotlinSourceCode, KotlinFunctionScanner)

    // when
    val extendedDiff = diffEngine.extendedDiff(patch)

    // then
    val patchedText = """
      fun a() {}
      fun b() {}
    """.trimIndent()
    val comparisonResults = listOf(
      Added(ParseResult.wellFormedFunction("b", 2, 2, 1)),
      Unmodified(ParseResult.wellFormedFunction("a", 1, 1, 1)),
    )

    assertThat(extendedDiff)
      .isEqualTo(HasChanges(patchedText, comparisonResults))
  }

  @Test
  @DisabledOnOs(OS.WINDOWS)
  fun `it should apply successive patches and return an extended diff`() {
    // given
    val kotlinSourceCode = "fun a() {}"

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

    val diffEngine = ExtendedDiffEngine.newInstance(kotlinSourceCode, KotlinFunctionScanner)

    // when
    diffEngine.extendedDiff(patch1)
    val extendedDiff = diffEngine.extendedDiff(patch2)

    // then
    val snippetA = "fun a() {}"
    val patchedText = """
      fun b() {}
    """.trimIndent()

    val comparisonResults = listOf(
      Deleted(ParseResult.wellFormedFunction("a", 1, 1, 1), snippetA),
      Unmodified(ParseResult.wellFormedFunction("b", 1, 1, 1)),
    )

    assertThat(extendedDiff)
      .isEqualTo(HasChanges(patchedText, comparisonResults))
  }

  @Test
  @DisabledOnOs(OS.WINDOWS)
  fun `it should get an extended diff for the Swift programming language`() {
    // given
    val swiftSourceCode = """
      class SomeClass {
        func a() {
        }
      }
    """.trimIndent()
    val patch = """
      --- a.txt	2021-04-21 14:55:44.000000000 +0530
      +++ b.txt	2021-04-21 14:55:54.000000000 +0530
      @@ -1,4 +1,5 @@
       class SomeClass {
         func a() {
      +    // Hello, world!
         }
       }
      \ No newline at end of file
    """.trimIndent()

    val diffEngine = ExtendedDiffEngine.newInstance(swiftSourceCode, SwiftFunctionScanner)

    // when
    val extendedDiff = diffEngine.extendedDiff(patch)

    // then
    val patchedText = """
      class SomeClass {
        func a() {
          // Hello, world!
        }
      }
    """.trimIndent()
    val snippetA = """
      |  func a() {
      |    // Hello, world!
      |  }
    """.trimMargin("|")
    val comparisonResults = listOf(
      Modified(ParseResult.wellFormedFunction("a", 2, 4, 1), snippetA)
    )

    assertThat(extendedDiff)
      .isEqualTo(HasChanges(patchedText, comparisonResults))
  }

  @Test
  @UseReporter(QuietReporter::class)
  fun `apply git patches from a real project`() {
    // given
    val resourceDirectory = "/fixtures/timelapse/"
    val seedSourceCode = readResourceText("${resourceDirectory}seed.txt")
    val patch1Text = readResourceText("${resourceDirectory}01.patch")
    val patch2Text = readResourceText("${resourceDirectory}02.patch")
    val patches = listOf(patch1Text, patch2Text)

    val diffEngine = ExtendedDiffEngine.newInstance(seedSourceCode, KotlinFunctionScanner)

    // when
    var patchedSourceCode = seedSourceCode
    patches.onEach { patch ->
      patchedSourceCode = (diffEngine.extendedDiff(patch) as HasChanges).sourceCode
    }

    // then
    Approvals.verify(patchedSourceCode)
  }
}
