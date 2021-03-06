package io.redgreen.timelapse.affectedfiles.view.model

import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Added
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Deleted
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Modified
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Moved
import io.redgreen.timelapse.affectedfiles.view.model.AffectedFileCellViewModel.DirectoryCell
import io.redgreen.timelapse.affectedfiles.view.model.AffectedFileCellViewModel.FileCell
import io.redgreen.timelapse.core.TrackedFilePath
import org.junit.jupiter.api.Test

internal class AffectedFileToCellViewModelMapperTest {
  @Test
  fun `group files based on directories they belong`() {
    // given
    val affectedFiles = listOf(
      Moved(TrackedFilePath("module/a/C.txt"), TrackedFilePath("module/b/C.txt"), 2, 4),
      Deleted(TrackedFilePath("module/a/D.txt"), 1),
      Modified(TrackedFilePath("module/a/B.txt"), 76, 12),
      Added(TrackedFilePath("module/a/A.txt"), 34),
    )

    // when
    val viewModels = AffectedFileToCellViewModelMapper.map(affectedFiles)

    // then
    assertThat(viewModels)
      .containsExactly(
        DirectoryCell("module/a/", 4),
        FileCell(Added(TrackedFilePath("module/a/A.txt"), 34)),
        FileCell(Modified(TrackedFilePath("module/a/B.txt"), 76, 12)),
        FileCell(Moved(TrackedFilePath("module/a/C.txt"), TrackedFilePath("module/b/C.txt"), 2, 4)),
        FileCell(Deleted(TrackedFilePath("module/a/D.txt"), 1)),
      )
      .inOrder()
  }

  @Test
  fun `group files in descending order based on number of files changed`() {
    // given
    val affectedFiles = listOf(
      Modified(TrackedFilePath("module/b/2.txt"), 5, 12),
      Moved(TrackedFilePath("module/a/C.txt"), TrackedFilePath("module/b/C.txt"), 2, 4),
      Modified(TrackedFilePath("module/b/1.txt"), 90, 52),
      Added(TrackedFilePath("module/a/A.txt"), 34),
      Deleted(TrackedFilePath("module/b/3.txt"), 89),
      Deleted(TrackedFilePath("module/a/D.txt"), 1),
      Modified(TrackedFilePath("module/a/B.txt"), 76, 12),
    )

    // when
    val viewModels = AffectedFileToCellViewModelMapper.map(affectedFiles)

    // then
    assertThat(viewModels)
      .containsExactly(
        DirectoryCell("module/a/", 4),
        FileCell(Added(TrackedFilePath("module/a/A.txt"), 34)),
        FileCell(Modified(TrackedFilePath("module/a/B.txt"), 76, 12)),
        FileCell(Moved(TrackedFilePath("module/a/C.txt"), TrackedFilePath("module/b/C.txt"), 2, 4)),
        FileCell(Deleted(TrackedFilePath("module/a/D.txt"), 1)),

        DirectoryCell("module/b/", 3),
        FileCell(Modified(TrackedFilePath("module/b/1.txt"), 90, 52)),
        FileCell(Modified(TrackedFilePath("module/b/2.txt"), 5, 12)),
        FileCell(Deleted(TrackedFilePath("module/b/3.txt"), 89)),
      )
      .inOrder()
  }

  @Test
  fun `group files in the root directory`() {
    // given
    val affectedFiles = listOf(
      Moved(TrackedFilePath("C.txt"), TrackedFilePath("3.txt"), 2, 4),
      Deleted(TrackedFilePath("D.txt"), 1),
      Modified(TrackedFilePath("B.txt"), 76, 12),
      Added(TrackedFilePath("A.txt"), 34),
    )

    // when
    val viewModels = AffectedFileToCellViewModelMapper.map(affectedFiles)

    // then
    assertThat(viewModels)
      .containsExactly(
        DirectoryCell("/", 4),
        FileCell(Added(TrackedFilePath("A.txt"), 34)),
        FileCell(Modified(TrackedFilePath("B.txt"), 76, 12)),
        FileCell(Moved(TrackedFilePath("C.txt"), TrackedFilePath("3.txt"), 2, 4)),
        FileCell(Deleted(TrackedFilePath("D.txt"), 1)),
      )
      .inOrder()
  }
}
