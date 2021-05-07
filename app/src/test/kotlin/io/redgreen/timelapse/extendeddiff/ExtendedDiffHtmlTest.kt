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
      Added(ParseResult.wellFormedFunction("b", 2, 2))
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
      Modified(ParseResult.wellFormedFunction("a", 2, 4), snippetA)
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
      Deleted(ParseResult.wellFormedFunction("a", 1, 2), snippetA)
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
        
        func b() {
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
      Modified(ParseResult.wellFormedFunction("a", 2, 4), snippetA),
      Added(ParseResult.wellFormedFunction("b", 6, 8)),
    )

    val hasChanges = HasChanges(swiftSource, comparisonResults)

    // when & then
    Approvals.verifyHtml(hasChanges.toHtml())
  }

  @Test
  fun `it should handle added, modified and deleted functions`() {
    // given
    val kotlinSource = """
      fun b() {
      }
      
      
      fun d() {
        println("Knock, knock!")
      }
      
    """.trimIndent()

    val functionA = """
      fun a() {
        // Nothing here...
      }
    """.trimIndent()
    val functionB = """
      fun b() {
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
      Deleted(ParseResult.wellFormedFunction("a", 1, 3), functionA),
      Modified(ParseResult.wellFormedFunction("b", 1, 2), functionB),
      Deleted(ParseResult.wellFormedFunction("c", 4, 6), functionC),
      Added(ParseResult.wellFormedFunction("d", 5, 7)),
      Deleted(ParseResult.wellFormedFunction("e", 8, 10), functionE)
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
      Added(ParseResult.wellFormedFunction("newlyAddedFunction", 1, 3)),
      Deleted(ParseResult.wellFormedFunction("deletedFunction", 2, 3), deletedFunction),
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
      Added(ParseResult.wellFormedFunction("modifiedFunction", 1, 3)),
      Deleted(ParseResult.wellFormedFunction("deletedFunction", 2, 3), deletedFunction),
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
      fun b() {
      }
      
      
      fun d() {
        println("Knock, knock!")
      }
      
    """.trimIndent()

    val functionA = """
      fun a() {
        // Nothing here...
      }
    """.trimIndent()
    val functionB = """
      fun b() {
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
      Deleted(ParseResult.wellFormedFunction("e", 8, 10), functionE),
      Added(ParseResult.wellFormedFunction("d", 5, 7)),
      Deleted(ParseResult.wellFormedFunction("c", 4, 6), functionC),
      Deleted(ParseResult.wellFormedFunction("a", 1, 3), functionA),
      Modified(ParseResult.wellFormedFunction("b", 1, 2), functionB)
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
      Deleted(ParseResult.wellFormedFunction("getAuthor", 2, 4), getAuthorFunction),
      Unmodified(ParseResult.wellFormedFunction("getReview", 1, 5)),
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
      Renamed(ParseResult.wellFormedFunction("add", 1, 3), "plus")
    )
    val hasChanges = HasChanges(kotlinSource, comparisonResults)

    // when & then
    Approvals.verifyHtml(hasChanges.toHtml())
  }

  @Test
  fun `it should add bold to function with modifier`() {
    // given
    val kotlinSource = """
      private inline fun greet() = println("Hello, world")
    """.trimIndent()

    val comparisonResults = listOf(
      Renamed(ParseResult.wellFormedFunction("greet", 1, 1), "helloWorld")
    )
    val hasChanges = HasChanges(kotlinSource, comparisonResults)

    // when & then
    Approvals.verifyHtml(hasChanges.toHtml())
  }

  @Test
  fun `it should add readability lines between deleted functions`() {
    // given
    val kotlinSource = """
      fun a() {}
    """.trimIndent()

    val comparisonResults = listOf(
      Unmodified(ParseResult.wellFormedFunction("a", 1, 1)),
      Deleted(ParseResult.wellFormedFunction("b", 3, 3), "fun b() {}"),
      Deleted(ParseResult.wellFormedFunction("c", 5, 5), "fun c() {}")
    )
    val hasChanges = HasChanges(kotlinSource, comparisonResults)

    // when & then
    Approvals.verifyHtml(hasChanges.toHtml())
  }

  @Test
  fun `it should not crash if the first line of the function does not contain the function name`() {
    // given
    val kotlinSource = """
      @SuppressWarnings("NothingReally")
      private inline fun greet() =
        println("Hello, world")
    """.trimIndent()

    val comparisonResults = listOf(
      Renamed(ParseResult.wellFormedFunction("greet", 1, 3), "helloWorld")
    )
    val hasChanges = HasChanges(kotlinSource, comparisonResults)

    // when & then
    Approvals.verifyHtml(hasChanges.toHtml())
  }
}
