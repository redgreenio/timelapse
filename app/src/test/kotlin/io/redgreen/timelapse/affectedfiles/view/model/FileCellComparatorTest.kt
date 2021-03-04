package io.redgreen.timelapse.affectedfiles.view.model

import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Added
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Deleted
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Modified
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Moved
import io.redgreen.timelapse.affectedfiles.view.model.AffectedFileCellViewModel.FileCell
import org.junit.jupiter.api.Test

internal class FileCellComparatorTest {
  @Test
  fun `sort file cells in the following order (NEW) - (Modified) - (Moved) - (Deleted)`() {
    // given
    val fileCells = listOf(
      FileCell(Deleted("NoLongerRequired.kt", 217)),
      FileCell(Added("Hello.kt", 12)),
      FileCell(Modified("NoLastingChanges.kt", 12, 99)),
      FileCell(Moved("BelongsHereNow.kt", "DoesNotBelongHere.kt", 1, 1)),
    ).toMutableList()

    // when
    val sortedFileCells = fileCells.sortedWith(FileCellComparator)

    // then
    assertThat(sortedFileCells)
      .containsExactly(
        FileCell(Added("Hello.kt", 12)),
        FileCell(Modified("NoLastingChanges.kt", 12, 99)),
        FileCell(Moved("BelongsHereNow.kt", "DoesNotBelongHere.kt", 1, 1)),
        FileCell(Deleted("NoLongerRequired.kt", 217)),
      )
      .inOrder()
  }

  @Test
  fun `sort file cells of the same type by change count`() {
    // given
    val fileCellsOfSameType = listOf(
      FileCell(Modified("ImmutableSortedSet.kt", 12, 1)),
      FileCell(Modified("SortedMap.kt", 1, 1)),
      FileCell(Modified("HashSet.kt", 11, 45)),
      FileCell(Modified("MutableSortedSet.kt", 71, 12)),
    ).toMutableList()

    // when
    val sortedFileCells = fileCellsOfSameType.sortedWith(FileCellComparator)

    // then
    assertThat(sortedFileCells)
      .containsExactly(
        FileCell(Modified("MutableSortedSet.kt", 71, 12)),
        FileCell(Modified("HashSet.kt", 11, 45)),
        FileCell(Modified("ImmutableSortedSet.kt", 12, 1)),
        FileCell(Modified("SortedMap.kt", 1, 1)),
      )
      .inOrder()
  }

  @Test
  fun `sort file cells of different types by change count`() {
    // given
    val fileCellsOfDifferentTypes = listOf(
      FileCell(Added("NB.kt", 6)),
      FileCell(Moved("VA.kt", "XA.kt", 12, 11)),
      FileCell(Deleted("DA.kt", 111)),
      FileCell(Added("NA.kt", 71)),
      FileCell(Moved("VB.kt", "XB.kt", 14, 7)),
      FileCell(Modified("MB.kt", 7, 3)),
      FileCell(Deleted("DB.kt", 86)),
      FileCell(Modified("MA.kt", 12, 1)),
    ).toMutableList()

    // when
    val sortedFileCells = fileCellsOfDifferentTypes.sortedWith(FileCellComparator)

    // then
    assertThat(sortedFileCells)
      .containsExactly(
        FileCell(Added("NA.kt", 71)),
        FileCell(Added("NB.kt", 6)),
        FileCell(Modified("MA.kt", 12, 1)),
        FileCell(Modified("MB.kt", 7, 3)),
        FileCell(Moved("VA.kt", "XA.kt", 12, 11)),
        FileCell(Moved("VB.kt", "XB.kt", 14, 7)),
        FileCell(Deleted("DA.kt", 111)),
        FileCell(Deleted("DB.kt", 86)),
      )
      .inOrder()
  }

  @Test
  fun `sort files of the same type with similar change count based on deletions`() {
    // given
    val fileCellsWithSimilarChangeCount = listOf(
      FileCell(Modified("MB.kt", 2, 2)),
      FileCell(Modified("MC.kt", 1, 3)),
      FileCell(Modified("MA.kt", 3, 1)),
    ).toMutableList()

    // when
    val sortedFileCells = fileCellsWithSimilarChangeCount.sortedWith(FileCellComparator)

    // then
    assertThat(sortedFileCells)
      .containsExactly(
        FileCell(Modified("MA.kt", 3, 1)),
        FileCell(Modified("MB.kt", 2, 2)),
        FileCell(Modified("MC.kt", 1, 3)),
      )
      .inOrder()
  }

  @Test
  fun `sort files of the same type with similar change count based on insertions if they don't have deletions`() {
    // given
    val fileCellsWithSimilarChangeCount = listOf(
      FileCell(Added("NB.kt", 2)),
      FileCell(Added("NC.kt", 1)),
      FileCell(Added("NA.kt", 3)),
    ).toMutableList()

    // when
    val sortedFileCells = fileCellsWithSimilarChangeCount.sortedWith(FileCellComparator)

    // then
    assertThat(sortedFileCells)
      .containsExactly(
        FileCell(Added("NA.kt", 3)),
        FileCell(Added("NB.kt", 2)),
        FileCell(Added("NC.kt", 1)),
      )
      .inOrder()
  }

  @Test
  fun `sort files of the same type with equal deletions and insertions based on filenames`() {
    // given
    val fileCellsWithSimilarChangeCount = listOf(
      FileCell(Modified("MB.kt", 3, 4)),
      FileCell(Modified("MC.kt", 3, 4)),
      FileCell(Modified("MA.kt", 3, 4)),
    ).toMutableList()

    // when
    val sortedFileCells = fileCellsWithSimilarChangeCount.sortedWith(FileCellComparator)

    // then
    assertThat(sortedFileCells)
      .containsExactly(
        FileCell(Modified("MA.kt", 3, 4)),
        FileCell(Modified("MB.kt", 3, 4)),
        FileCell(Modified("MC.kt", 3, 4)),
      )
      .inOrder()
  }
}
