package redgreen.dawn.affectedfiles.view.model

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import redgreen.dawn.affectedfiles.model.AffectedFile.Deleted
import redgreen.dawn.affectedfiles.model.AffectedFile.Modified
import redgreen.dawn.affectedfiles.model.AffectedFile.Moved
import redgreen.dawn.affectedfiles.model.AffectedFile.New
import redgreen.dawn.affectedfiles.view.model.AffectedFileCellViewModel.DirectoryCell
import redgreen.dawn.affectedfiles.view.model.AffectedFileCellViewModel.FileCell

internal class AffectedFileToCellViewModelMapperTest {
  @Test
  fun `group files based on directories they belong`() {
    // given
    val affectedFiles = listOf(
      Moved("module/a/C.txt", 2, 4),
      Deleted("module/a/D.txt", 1),
      Modified("module/a/B.txt", 76, 12),
      New("module/a/A.txt", 34),
    )

    // when
    val viewModels = AffectedFileToCellViewModelMapper.map(affectedFiles)

    // then
    assertThat(viewModels)
      .containsExactly(
        DirectoryCell("module/a/", 4),
        FileCell(New("module/a/A.txt", 34)),
        FileCell(Modified("module/a/B.txt", 76, 12)),
        FileCell(Moved("module/a/C.txt", 2, 4)),
        FileCell(Deleted("module/a/D.txt", 1)),
      )
      .inOrder()
  }

  @Test
  fun `group files in descending order based on number of files changed`() {
    // given
    val affectedFiles = listOf(
      Modified("module/b/2.txt", 5, 12),
      Moved("module/a/C.txt", 2, 4),
      Modified("module/b/1.txt", 90, 52),
      New("module/a/A.txt", 34),
      Deleted("module/b/3.txt", 89),
      Deleted("module/a/D.txt", 1),
      Modified("module/a/B.txt", 76, 12),
    )

    // when
    val viewModels = AffectedFileToCellViewModelMapper.map(affectedFiles)

    // then
    assertThat(viewModels)
      .containsExactly(
        DirectoryCell("module/a/", 4),
        FileCell(New("module/a/A.txt", 34)),
        FileCell(Modified("module/a/B.txt", 76, 12)),
        FileCell(Moved("module/a/C.txt", 2, 4)),
        FileCell(Deleted("module/a/D.txt", 1)),

        DirectoryCell("module/b/", 3),
        FileCell(Modified("module/b/1.txt", 90, 52)),
        FileCell(Modified("module/b/2.txt", 5, 12)),
        FileCell(Deleted("module/b/3.txt", 89)),
      )
      .inOrder()
  }

  @Test
  fun `group files in the root directory`() {
    // given
    val affectedFiles = listOf(
      Moved("C.txt", 2, 4),
      Deleted("D.txt", 1),
      Modified("B.txt", 76, 12),
      New("A.txt", 34),
    )

    // when
    val viewModels = AffectedFileToCellViewModelMapper.map(affectedFiles)

    // then
    assertThat(viewModels)
      .containsExactly(
        DirectoryCell("/", 4),
        FileCell(New("A.txt", 34)),
        FileCell(Modified("B.txt", 76, 12)),
        FileCell(Moved("C.txt", 2, 4)),
        FileCell(Deleted("D.txt", 1)),
      )
      .inOrder()
  }
}
