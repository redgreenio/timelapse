package io.redgreen.timelapse.affectedfiles.view.model

import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.affectedfiles.view.model.AffectedFileCellViewModel.FileCell
import io.redgreen.timelapse.git.model.AffectedFile.Added
import io.redgreen.timelapse.git.model.AffectedFile.Deleted
import io.redgreen.timelapse.git.model.AffectedFile.Modified
import io.redgreen.timelapse.git.model.AffectedFile.Moved
import io.redgreen.timelapse.git.model.TrackedFilePath
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

typealias TestPair = Pair<List<AffectedFileCellViewModel>, String>

class AffectedFilesSummaryTest {
  @Suppress("unused") // because static functions are used by parameterized tests
  companion object {
    @JvmStatic
    fun oneAffectedFileParams(): List<TestPair> {
      return listOf(
        listOf(FileCell(Added(TrackedFilePath("Hello.txt"), 12))) to "1 added",
        listOf(FileCell(Modified(TrackedFilePath("BeenHere.txt"), 96, 11))) to "1 modified",
        listOf(FileCell(Moved(TrackedFilePath("InTheRightPlace.txt"), TrackedFilePath("WasHere.txt"), 1, 1))) to "1 moved",
        listOf(FileCell(Deleted(TrackedFilePath("ServedMyPurpose.txt"), 101))) to "1 deleted",
      )
    }

    @JvmStatic
    fun moreThanOneHomogeneousAffectedFileParams(): List<TestPair> {
      return listOf(
        listOf(
          FileCell(Added(TrackedFilePath("1.txt"), 1)),
          FileCell(Added(TrackedFilePath("2.txt"), 1))
        ) to "2 added",

        listOf(
          FileCell(Modified(TrackedFilePath("1.txt"), 5, 16)),
          FileCell(Modified(TrackedFilePath("2.txt"), 11, 66)),
          FileCell(Modified(TrackedFilePath("3.txt"), 98, 17)),
        ) to "3 modified",

        listOf(
          FileCell(Moved(TrackedFilePath("1.txt"), TrackedFilePath("A.txt"), 1, 1)),
          FileCell(Moved(TrackedFilePath("2.txt"), TrackedFilePath("B.txt"), 11, 66)),
          FileCell(Moved(TrackedFilePath("3.txt"), TrackedFilePath("C.txt"), 98, 17)),
          FileCell(Moved(TrackedFilePath("4.txt"), TrackedFilePath("D.txt"), 11, 11)),
        ) to "4 moved",

        listOf(
          FileCell(Deleted(TrackedFilePath("1.txt"), 1)),
          FileCell(Deleted(TrackedFilePath("4.txt"), 11)),
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
      FileCell(Added(TrackedFilePath("1.txt"), 1)),
      FileCell(Modified(TrackedFilePath("2.txt"), 11, 66)),
      FileCell(Modified(TrackedFilePath("3.txt"), 98, 17)),
      FileCell(Moved(TrackedFilePath("A.txt"), TrackedFilePath("1.txt"), 1, 1)),
      FileCell(Moved(TrackedFilePath("B.txt"), TrackedFilePath("2.txt"), 11, 66)),
      FileCell(Moved(TrackedFilePath("C.txt"), TrackedFilePath("3.txt"), 98, 17)),
      FileCell(Moved(TrackedFilePath("D.txt"), TrackedFilePath("4.txt"), 11, 11)),
      FileCell(Deleted(TrackedFilePath("X.txt"), 1)),
      FileCell(Deleted(TrackedFilePath("Y.txt"), 11)),
    )

    // when & then
    assertThat(affectedFileCellViewModels.summarize())
      .isEqualTo("9 files affected • 1 added, 2 modified, 4 moved, 2 deleted")
  }
}
