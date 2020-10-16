package io.redgreen.timelapse.git

import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.domain.openGitRepository
import io.redgreen.timelapse.git.FileChange.Addition
import io.redgreen.timelapse.git.FileChange.Deletion
import io.redgreen.timelapse.git.FileChange.Modification
import io.redgreen.timelapse.git.FileChange.Rename
import org.junit.jupiter.api.Test
import java.io.File

class GitFunctionsTest {
  private val repository = openGitRepository(File("git-testbed"))

  @Test
  fun `it should get a list of files from initial commit`() {
    // given
    val initialCommitId = "b6748190194e697df97d3dd9801af4f55d763ef9" // exhibit a: add three new files

    // when
    val filesInCommit = repository.getFilesInCommit(initialCommitId)

    // then
    assertThat(filesInCommit)
      .containsExactly(
        Addition("file-1.txt"),
        Addition("file-2.txt"),
        Addition("file-3.txt"),
      )
  }

  @Test
  fun `it should get added file from a non-initial commit`() {
    // given
    val commitIdWithNewFile = "b0d86a6cf1f8c9a12b25f2f51f5be97b61647075" // exhibit b: add a new file

    // when
    val filesInCommit = repository.getFilesInCommit(commitIdWithNewFile)

    // then
    assertThat(filesInCommit)
      .containsExactly(
        Addition("file-4.txt")
      )
  }

  @Test
  fun `it should get modified file from a non-initial commit`() {
    // given
    val commitIdWithModifiedFile = "6c2faf72204d1848bdaef44f4e69c2c4ae6ca786" // exhibit c: modify a file

    // when
    val filesInCommit = repository.getFilesInCommit(commitIdWithModifiedFile)

    // then
    assertThat(filesInCommit)
      .containsExactly(
        Modification("file-1.txt")
      )
  }

  @Test
  fun `it should get a deleted file from a non-initial commit`() {
    // given
    val commitIdWithDeletedFile = "68958540148efb4dd0dbfbb181df330deaffbe13" // exhibit d: delete a file

    // when
    val filesInCommit = repository.getFilesInCommit(commitIdWithDeletedFile)

    // then
    assertThat(filesInCommit)
      .containsExactly(
        Deletion("file-4.txt")
      )
  }

  @Test
  fun `it should get a renamed file from a non-initial commit`() {
    // given
    val commitIdWithRenamedFile = "f1027401b8d62cd699f286b8eb8e049645654909" // exhibit f: rename a file

    // when
    val filesInCommit = repository.getFilesInCommit(commitIdWithRenamedFile)

    // then
    assertThat(filesInCommit)
      .containsExactly(
        Rename("file-4.txt", "file-1-copy.txt")
      )
  }

  @Test
  fun `it should get renamed, added, modified and deleted files from a non-initial commit`() {
    // given
    val commitId = "374bbc8b4cefbb6c37feb5526a68f5d7bf0aeb7f" // exhibit g: renames, deletion, addition and modification

    // when
    val filesInCommit = repository.getFilesInCommit(commitId)

    // then
    assertThat(filesInCommit)
      .containsExactly(
        Modification("file-1.txt"),
        Rename("file-a.txt", "file-2.txt"),
        Addition("file-b.txt"),
        Addition("file-c.txt"),
        Deletion("file-3.txt"),
        Deletion("file-4.txt"),
      )
  }
}
