package io.redgreen.timelapse.git

import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.domain.getDiff
import io.redgreen.timelapse.domain.openGitRepository
import io.redgreen.timelapse.vcs.ChangedFile.Addition
import io.redgreen.timelapse.vcs.ChangedFile.Deletion
import io.redgreen.timelapse.vcs.ChangedFile.Modification
import io.redgreen.timelapse.vcs.ChangedFile.Rename
import org.junit.jupiter.api.Test
import java.io.File

class GitFunctionsTest {
  private val repository = openGitRepository(File("git-testbed"))

  @Test
  fun `it should get a list of files from initial commit`() {
    // given
    val initialCommitId = "b6748190194e697df97d3dd9801af4f55d763ef9" // exhibit a: add three new files

    // when
    val changedFilesInCommit = repository.getChangedFilesInCommit(initialCommitId)

    // then
    assertThat(changedFilesInCommit)
      .containsExactly(
        Addition("file-1.txt"),
        Addition("file-2.txt"),
        Addition("file-3.txt"),
      )
  }

  @Test
  fun `it should get added file from a non-initial commit`() {
    // given
    val newFileCommitId = "b0d86a6cf1f8c9a12b25f2f51f5be97b61647075" // exhibit b: add a new file

    // when
    val changedFilesInCommit = repository.getChangedFilesInCommit(newFileCommitId)

    // then
    assertThat(changedFilesInCommit)
      .containsExactly(
        Addition("file-4.txt")
      )
  }

  @Test
  fun `it should get modified file from a non-initial commit`() {
    // given
    val modifiedFileCommitId = "6c2faf72204d1848bdaef44f4e69c2c4ae6ca786" // exhibit c: modify a file

    // when
    val changedFilesInCommit = repository.getChangedFilesInCommit(modifiedFileCommitId)

    // then
    assertThat(changedFilesInCommit)
      .containsExactly(
        Modification("file-1.txt")
      )
  }

  @Test
  fun `it should get a deleted file from a non-initial commit`() {
    // given
    val deletedFileCommitId = "68958540148efb4dd0dbfbb181df330deaffbe13" // exhibit d: delete a file

    // when
    val changedFilesInCommit = repository.getChangedFilesInCommit(deletedFileCommitId)

    // then
    assertThat(changedFilesInCommit)
      .containsExactly(
        Deletion("file-4.txt")
      )
  }

  @Test
  fun `it should get a renamed file from a non-initial commit`() {
    // given
    val renamedFileCommitId = "f1027401b8d62cd699f286b8eb8e049645654909" // exhibit f: rename a file

    // when
    val changedFilesInCommit = repository.getChangedFilesInCommit(renamedFileCommitId)

    // then
    assertThat(changedFilesInCommit)
      .containsExactly(
        Rename("file-4.txt", "file-1-copy.txt")
      )
  }

  @Test
  fun `it should get renamed, added, modified and deleted files from a non-initial commit`() {
    // given
    val commitId = "374bbc8b4cefbb6c37feb5526a68f5d7bf0aeb7f" // exhibit g: renames, deletion, addition and modification

    // when
    val changedFilesInCommit = repository.getChangedFilesInCommit(commitId)

    // then
    assertThat(changedFilesInCommit)
      .containsExactly(
        Modification("file-1.txt"),
        Rename("file-a.txt", "file-2.txt"),
        Addition("file-b.txt"),
        Addition("file-c.txt"),
        Deletion("file-3.txt"),
        Deletion("file-4.txt"),
      )
  }

  @Test
  fun `it should get the diff of a file that was newly added`() {
    // given
    val newlyAddedFileCommitId = "0e298ab233af0e283edff96772c75a42a21b1479" // exhibit e: copy a file

    // when
    val newlyAddedFileDiff = repository.getDiff(newlyAddedFileCommitId, "file-1-copy.txt")

    // then
    assertThat(newlyAddedFileDiff)
      .isEqualTo(
        """
          diff --git a/file-1-copy.txt b/file-1-copy.txt
          new file mode 100644
          index 0000000..980a0d5
          --- /dev/null
          +++ b/file-1-copy.txt
          @@ -0,0 +1 @@
          +Hello World!
          
        """.trimIndent()
      )
  }

  @Test
  fun `it should get the diff of a deleted file`() {
    // given
    val deletedFileCommitId =
      "374bbc8b4cefbb6c37feb5526a68f5d7bf0aeb7f" // exhibit g: renames, deletion, addition and modification

    // when
    val deletedFileDiff = repository.getDiff(deletedFileCommitId, "file-4.txt")

    // then
    assertThat(deletedFileDiff)
      .isEqualTo(
        """
          diff --git a/file-4.txt b/file-4.txt
          deleted file mode 100644
          index 980a0d5..0000000
          --- a/file-4.txt
          +++ /dev/null
          @@ -1 +0,0 @@
          -Hello World!
          
        """.trimIndent()
      )
  }

  @Test
  fun `it should get diff of a modified file`() {
    // given
    val modifiedFileCommitId =
      "374bbc8b4cefbb6c37feb5526a68f5d7bf0aeb7f" // exhibit g: renames, deletion, addition and modification

    // when
    val modifiedFileDiff = repository.getDiff(modifiedFileCommitId, "file-1.txt")

    // then
    assertThat(modifiedFileDiff)
      .isEqualTo(
        """
          diff --git a/file-1.txt b/file-1.txt
          index 980a0d5..d502583 100644
          --- a/file-1.txt
          +++ b/file-1.txt
          @@ -1 +1 @@
          -Hello World!
          +Hello Universe!
          
        """.trimIndent()
      )
  }

  @Test
  fun `it should get diff of a renamed file`() {
    // given
    val renamedFileCommitId = "f1027401b8d62cd699f286b8eb8e049645654909" // exhibit f: rename a file

    // when
    val renamedFileDiff = repository.getDiff(renamedFileCommitId, "file-4.txt")

    // then
    assertThat(renamedFileDiff)
      .isEqualTo(
        """
          diff --git a/file-4.txt b/file-4.txt
          new file mode 100644
          index 0000000..980a0d5
          --- /dev/null
          +++ b/file-4.txt
          @@ -0,0 +1 @@
          +Hello World!
          
        """.trimIndent()
      )
  }

  @Test
  fun `it should get diff for a file added in the first commit`() {
    // given
    val initialCommit = "b6748190194e697df97d3dd9801af4f55d763ef9" // exhibit a: add three new files

    // when
    val newFileDiff = repository.getDiff(initialCommit, "file-1.txt")

    // then
    assertThat(newFileDiff)
      .isEqualTo(
        """
          diff --git a/file-1.txt b/file-1.txt
          new file mode 100644
          index 0000000..e69de29
          --- /dev/null
          +++ b/file-1.txt
          
        """.trimIndent()
      )
  }

  @Test
  fun `it should get the diff of a deleted file with no content`() {
    // given
    val deletedFileCommitId =
      "374bbc8b4cefbb6c37feb5526a68f5d7bf0aeb7f" // exhibit g: renames, deletion, addition and modification

    // when
    val deletedEmptyFileDiff = repository.getDiff(deletedFileCommitId, "file-3.txt")

    // then
    assertThat(deletedEmptyFileDiff)
      .isEqualTo(
        """
          diff --git a/file-3.txt b/file-3.txt
          deleted file mode 100644
          index e69de29..0000000
          --- a/file-3.txt
          +++ /dev/null
          
        """.trimIndent()
      )
  }

  @Test
  fun `it should get the diff of a new file with no content`() {
    // given
    val newEmptyFileCommitId =
      "374bbc8b4cefbb6c37feb5526a68f5d7bf0aeb7f" // exhibit g: renames, deletion, addition and modification

    // when
    val newEmptyFileDiff = repository.getDiff(newEmptyFileCommitId, "file-b.txt")

    // then
    assertThat(newEmptyFileDiff)
      .isEqualTo(
        """
          diff --git a/file-b.txt b/file-b.txt
          new file mode 100644
          index 0000000..e69de29
          --- /dev/null
          +++ b/file-b.txt
          
        """.trimIndent()
      )
  }
}
