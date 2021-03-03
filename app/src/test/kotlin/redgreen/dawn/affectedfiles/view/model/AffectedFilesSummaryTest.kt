package redgreen.dawn.affectedfiles.view.model;

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import redgreen.dawn.affectedfiles.model.AffectedFile.Deleted
import redgreen.dawn.affectedfiles.model.AffectedFile.Modified
import redgreen.dawn.affectedfiles.model.AffectedFile.Moved
import redgreen.dawn.affectedfiles.model.AffectedFile.New
import redgreen.dawn.affectedfiles.view.model.AffectedFileCellViewModel.FileCell

typealias TestPair = Pair<List<AffectedFileCellViewModel>, String>

class AffectedFilesSummaryTest {
  @Suppress("unused") // because static functions are used by parameterized tests
  companion object {
    @JvmStatic
    fun oneAffectedFileParams(): List<TestPair> {
      return listOf(
        listOf(FileCell(New("Hello.txt", 12))) to "1 new",
        listOf(FileCell(Modified("BeenHere.txt", 96, 11))) to "1 modified",
        listOf(FileCell(Moved("InTheRightPlace.txt", 1, 1))) to "1 moved",
        listOf(FileCell(Deleted("ServedMyPurpose.txt", 101))) to "1 deleted",
      )
    }

    @JvmStatic
    fun moreThanOneHomogeneousAffectedFileParams(): List<TestPair> {
      return listOf(
        listOf(
          FileCell(New("1.txt", 1)),
          FileCell(New("2.txt", 1))
        ) to "2 new",

        listOf(
          FileCell(Modified("1.txt", 5, 16)),
          FileCell(Modified("2.txt", 11, 66)),
          FileCell(Modified("3.txt", 98, 17)),
        ) to "3 modified",

        listOf(
          FileCell(Moved("1.txt", 1, 1)),
          FileCell(Moved("2.txt", 11, 66)),
          FileCell(Moved("3.txt", 98, 17)),
          FileCell(Moved("4.txt", 11, 11)),
        ) to "4 moved",

        listOf(
          FileCell(Deleted("1.txt", 1)),
          FileCell(Deleted("4.txt", 11)),
        ) to "2 deleted",
      )
    }
  }

  @ParameterizedTest
  @MethodSource("oneAffectedFileParams")
  fun `summarize directories with one affected file`(testPair: TestPair) {
    // given
    val (affectedFileCellViewModel, summary) = testPair

    // when & then
    assertThat(affectedFileCellViewModel.summarize())
      .isEqualTo(summary)
  }

  @ParameterizedTest
  @MethodSource("moreThanOneHomogeneousAffectedFileParams")
  fun `summarize directories with more than one affected file of the same type`(
    testPair: TestPair
  ) {
    // given
    val (affectedFileCellViewModel, summary) = testPair

    // when & then
    assertThat(affectedFileCellViewModel.summarize())
      .isEqualTo(summary)
  }

  @Test
  fun `summarize heterogeneous affected files`() {
    // given
    val affectedFileCellViewModels = listOf(
      FileCell(New("1.txt", 1)),
      FileCell(Modified("2.txt", 11, 66)),
      FileCell(Modified("3.txt", 98, 17)),
      FileCell(Moved("A.txt", 1, 1)),
      FileCell(Moved("B.txt", 11, 66)),
      FileCell(Moved("C.txt", 98, 17)),
      FileCell(Moved("D.txt", 11, 11)),
      FileCell(Deleted("X.txt", 1)),
      FileCell(Deleted("Y.txt", 11)),
    )

    // when & then
    assertThat(affectedFileCellViewModels.summarize())
      .isEqualTo("9 files affected • 1 new, 2 modified, 4 moved, 2 deleted")
  }
}