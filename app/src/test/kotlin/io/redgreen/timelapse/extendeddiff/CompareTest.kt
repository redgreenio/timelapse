package io.redgreen.timelapse.extendeddiff

import com.google.common.truth.Truth.assertThat
import io.redgreen.scout.ParseResult
import io.redgreen.scout.languages.kotlin.KotlinFunctionScanner
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Added
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Deleted
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Modified
import org.junit.jupiter.api.Test

class CompareTest {
  @Test
  fun `it should detect an added function`() {
    // given
    val before = """
      fun a() {
      }
    """.trimIndent()

    val after = """
      fun a() {
      }

      fun b() {
      }
    """.trimIndent()

    // when
    val comparisonResults = compare(before, after, KotlinFunctionScanner)

    // then
    assertThat(comparisonResults)
      .containsExactly(
        Added(ParseResult.wellFormedFunction("b", 4, 5, 1))
      )
  }

  @Test
  fun `it should detect several added functions`() {
    // given
    val before = """
      fun a() {
      }
    """.trimIndent()

    val after = """
      fun a() {
      }

      fun b() {
      }

      fun c() {
      }
    """.trimIndent()

    // when
    val comparisonResults = compare(before, after, KotlinFunctionScanner)

    // then
    assertThat(comparisonResults)
      .containsExactly(
        Added(ParseResult.wellFormedFunction("b", 4, 5, 1)),
        Added(ParseResult.wellFormedFunction("c", 7, 8, 1))
      )
      .inOrder()
  }

  @Test
  fun `it should detect a deleted function`() {
    // given
    val before = """
      fun a() {
      }

      fun b() {
      }
    """.trimIndent()

    val after = """
      fun a() {
      }
    """.trimIndent()

    // when
    val comparisonResults = compare(before, after, KotlinFunctionScanner)

    // then
    val deletedFunctionSnippet = """
      fun b() {
      }
    """.trimIndent()
    assertThat(comparisonResults)
      .containsExactly(
        Deleted(ParseResult.wellFormedFunction("b", 4, 5, 1), deletedFunctionSnippet)
      )
  }

  @Test
  fun `it should detect several deleted functions`() {
    // given
    val before = """
      fun a() {
      }

      fun b() {
      }
    """.trimIndent()

    val after = "".trimIndent()

    // when
    val comparisonResults = compare(before, after, KotlinFunctionScanner)

    // then
    val snippetA = """
      fun a() {
      }
    """.trimIndent()

    val snippetB = """
      fun b() {
      }
    """.trimIndent()
    assertThat(comparisonResults)
      .containsExactly(
        Deleted(ParseResult.wellFormedFunction("a", 1, 2, 1), snippetA),
        Deleted(ParseResult.wellFormedFunction("b", 4, 5, 1), snippetB)
      )
  }

  @Test
  fun `it should detect a modified function`() {
    // given
    val before = """
      fun a() {
      }
    """.trimIndent()

    val after = """
      fun a() {
        println("Hello, world!")
      }
    """.trimIndent()

    // when
    val comparisonResults = compare(before, after, KotlinFunctionScanner)

    // then
    assertThat(comparisonResults)
      .containsExactly(
        Modified(ParseResult.wellFormedFunction("a", 1, 3, 1))
      )
  }

  @Test
  fun `it should detect multiple modified functions`() {
    // given
    val before = """
      fun a() {
      }

      fun b() {
      }
    """.trimIndent()

    val after = """
      fun b() {
      }

      fun a() {
      }
    """.trimIndent()

    // when
    val comparisonResults = compare(before, after, KotlinFunctionScanner)

    // then
    assertThat(comparisonResults)
      .containsExactly(
        Modified(ParseResult.wellFormedFunction("b", 1, 2, 1)),
        Modified(ParseResult.wellFormedFunction("a", 4, 5, 1))
      )
  }

  @Test
  fun `it should detect added, modified, and deleted functions`() {
    // given
    val before = """
      fun a() {
      }

      fun b() {
      }

      fun c() {
      }
    """.trimIndent()

    val after = """
      fun a() {
      }

      fun x() {
      }

      fun c() {
        println("Hello, world!")
      }
    """.trimIndent()

    // when
    val comparisonResults = compare(before, after, KotlinFunctionScanner)

    // then
    val snippetB = """
      fun b() {
      }
    """.trimIndent()

    assertThat(comparisonResults)
      .containsExactly(
        Deleted(ParseResult.wellFormedFunction("b", 4, 5, 1), snippetB),
        Added(ParseResult.wellFormedFunction("x", 4, 5, 1)),
        Modified(ParseResult.wellFormedFunction("c", 7, 9, 1))
      )
  }

  @Test
  fun `patch and compare integration`() {
    // given
    val source = """
      fun a() {
      }

      fun b() {
      }

      fun c() {
      }
    """.trimIndent()

    val patch = """
      @@ -1,8 +1,9 @@
       fun a() {
       }
       
      -fun b() {
      +fun x() {
       }
       
       fun c() {
      +  println("Hello, world!")
       }
      \ No newline at end of file
    """.trimIndent()

    // when
    val comparisonResults = patchAndCompare(source, patch, KotlinFunctionScanner)

    // then
    val snippetB = """
      fun b() {
      }
    """.trimIndent()

    assertThat(comparisonResults)
      .containsExactly(
        Deleted(ParseResult.wellFormedFunction("b", 4, 5, 1), snippetB),
        Added(ParseResult.wellFormedFunction("x", 4, 5, 1)),
        Modified(ParseResult.wellFormedFunction("c", 7, 9, 1))
      )
  }
}
