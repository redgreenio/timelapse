package io.redgreen.timelapse.extendeddiff

import io.redgreen.scout.ParseResult
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Added
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Deleted
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Modified
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Renamed
import io.redgreen.timelapse.extendeddiff.ComparisonResult.Unmodified
import io.redgreen.timelapse.extendeddiff.ExtendedDiff.HasChanges
import io.redgreen.timelapse.extendeddiff.ExtendedDiff.NoChanges
import org.approvaltests.Approvals
import org.approvaltests.reporters.QuietReporter
import org.approvaltests.reporters.UseReporter
import org.junit.jupiter.api.Test

@UseReporter(QuietReporter::class)
class ExtendedDiffHtmlTest {
  @Test
  fun `it should handle added function`() {
    // given
    val kotlinSource = """
      fun a() {}
      fun b() {}
    """.trimIndent()

    val comparisonResults = listOf(
      Added(ParseResult.wellFormedFunction("b", 2, 2, 1))
    )

    val hasChanges = HasChanges(kotlinSource, comparisonResults)

    // when & then
    Approvals.verifyHtml(hasChanges.toHtml())
  }

  @Test
  fun `it should handle modified function`() {
    // given
    val swiftSource = """
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

    val hasChanges = HasChanges(swiftSource, comparisonResults)

    // when & then
    Approvals.verifyHtml(hasChanges.toHtml())
  }

  @Test
  fun `it should handle deleted function`() {
    // given
    val kotlinSource = """
      fun b() {
      }
    """.trimIndent()

    val snippetA = """
      fun a() {
      }
    """.trimIndent()

    val comparisonResults = listOf(
      Deleted(ParseResult.wellFormedFunction("a", 1, 2, 1), snippetA)
    )

    val hasChanges = HasChanges(kotlinSource, comparisonResults)

    // when & then
    Approvals.verifyHtml(hasChanges.toHtml())
  }

  @Test
  fun `it should handle modified and added functions`() {
    // given
    val swiftSource = """
      class SomeClass {
        func a() {
          // Hello, world!
        }
        
        fun b() {
          println("I am new!")
        }
      }
    """.trimIndent()

    val snippetA = """
      |  func a() {
      |    // Hello, world!
      |  }
    """.trimIndent()
    val comparisonResults = listOf(
      Modified(ParseResult.wellFormedFunction("a", 2, 4, 1), snippetA),
      Added(ParseResult.wellFormedFunction("b", 6, 8, 1)),
    )

    val hasChanges = HasChanges(swiftSource, comparisonResults)

    // when & then
    Approvals.verifyHtml(hasChanges.toHtml())
  }

  @Test
  fun `it should handle added, modified and deleted functions`() {
    // given
    val kotlinSource = """
      function b() {
      }
      
      
      function d() {
        println("Knock, knock!")
      }
      
    """.trimIndent()

    val functionA = """
      fun a() {
        // Nothing here...
      }
    """.trimIndent()
    val functionB = """
      function b() {
      }
    """.trimIndent()
    val functionC = """
      fun c(a: Int, b: Int): Int {
        return a + b
      }
    """.trimIndent()
    val functionE = """
      fun e(a: Double, b: Double): Double {
        return a * b
      }
    """.trimIndent()

    val comparisonResults = listOf(
      Deleted(ParseResult.wellFormedFunction("a", 1, 3, 1), functionA),
      Modified(ParseResult.wellFormedFunction("b", 1, 2, 1), functionB),
      Deleted(ParseResult.wellFormedFunction("c", 4, 6, 1), functionC),
      Added(ParseResult.wellFormedFunction("d", 5, 7, 1)),
      Deleted(ParseResult.wellFormedFunction("e", 8, 10, 1), functionE)
    )

    // when
    val hasChanges = HasChanges(kotlinSource, comparisonResults)

    // then
    Approvals.verifyHtml(hasChanges.toHtml())
  }

  @Test
  fun `deleted functions should not overlap added functions`() {
    // given
    val kotlinSource = """
      fun newlyAddedFunction() {
        println("Hello, world!")
      }
    """.trimIndent()

    val deletedFunction = """
      fun deletedFunction() {
      }
    """.trimIndent()

    val comparisonResults = listOf(
      Added(ParseResult.wellFormedFunction("newlyAddedFunction", 1, 3, 1)),
      Deleted(ParseResult.wellFormedFunction("deletedFunction", 2, 3, 1), deletedFunction),
    )

    // when
    val hasChanges = HasChanges(kotlinSource, comparisonResults)

    // then
    Approvals.verifyHtml(hasChanges.toHtml())
  }

  @Test
  fun `deleted functions should not overlap modified functions`() {
    // given
    val kotlinSource = """
      fun modifiedFunction() {
        println("Hello, world!")
      }
    """.trimIndent()

    val deletedFunction = """
      fun deletedFunction() {
      }
    """.trimIndent()

    val comparisonResults = listOf(
      Added(ParseResult.wellFormedFunction("modifiedFunction", 1, 3, 1)),
      Deleted(ParseResult.wellFormedFunction("deletedFunction", 2, 3, 1), deletedFunction),
    )

    // when
    val hasChanges = HasChanges(kotlinSource, comparisonResults)

    // then
    Approvals.verifyHtml(hasChanges.toHtml())
  }

  @Test
  fun `it should handle comparison results in any order`() {
    // given
    val kotlinSource = """
      function b() {
      }
      
      
      function d() {
        println("Knock, knock!")
      }
      
    """.trimIndent()

    val functionA = """
      fun a() {
        // Nothing here...
      }
    """.trimIndent()
    val functionB = """
      function b() {
      }
    """.trimIndent()
    val functionC = """
      fun c(a: Int, b: Int): Int {
        return a + b
      }
    """.trimIndent()
    val functionE = """
      fun e(a: Double, b: Double): Double {
        return a * b
      }
    """.trimIndent()

    val comparisonResults = listOf(
      Deleted(ParseResult.wellFormedFunction("e", 8, 10, 1), functionE),
      Added(ParseResult.wellFormedFunction("d", 5, 7, 1)),
      Deleted(ParseResult.wellFormedFunction("c", 4, 6, 1), functionC),
      Deleted(ParseResult.wellFormedFunction("a", 1, 3, 1), functionA),
      Modified(ParseResult.wellFormedFunction("b", 1, 2, 1), functionB)
    )

    // when
    val hasChanges = HasChanges(kotlinSource, comparisonResults)

    // then
    Approvals.verifyHtml(hasChanges.toHtml())
  }

  @Test
  fun `it should render HTML for no changes`() {
    // given
    val kotlinSource = """
      fun sayHello() {
        println("Hey, hey!")
      }
    """.trimIndent()
    val noChanges = NoChanges(kotlinSource)

    // when & then
    Approvals.verifyHtml(noChanges.toHtml())
  }

  @Test
  fun `it should not overlap deleted functions in-between unchanged functions`() {
    // given
    val kotlinSource = """
      fun getReview(book: Book): Review {
        val rating = book.read()
        val ratingFromFriends = book.discussWithFriends()
        return Review(rating + ratingFromFriends)
      }
    """.trimIndent()

    val getAuthorFunction = """
      fun getAuthor(book: Book): Author {
        return book.author
      }
    """.trimIndent()

    val comparisonResults = listOf(
      Deleted(ParseResult.wellFormedFunction("getAuthor", 2, 4, 1), getAuthorFunction),
      Unmodified(ParseResult.wellFormedFunction("getReview", 1, 5, 1)),
    )
    val hasChanges = HasChanges(kotlinSource, comparisonResults)

    // when & then
    Approvals.verifyHtml(hasChanges.toHtml())
  }

  @Test
  fun `it should render renamed function`() {
    // given
    val kotlinSource = """
      fun add(x: Int, y: Int): Int {
        return x + y
      }
    """.trimIndent()

    val comparisonResults = listOf(
      Renamed(ParseResult.wellFormedFunction("add", 1, 3, 1), "plus")
    )
    val hasChanges = HasChanges(kotlinSource, comparisonResults)

    // when & then
    Approvals.verifyHtml(hasChanges.toHtml())
  }
}
