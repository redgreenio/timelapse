package io.redgreen.timelapse.git

import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.domain.BlobDiff.Merge
import io.redgreen.timelapse.domain.BlobDiff.Simple
import io.redgreen.timelapse.domain.getBlobDiff
import io.redgreen.timelapse.fixtures.GitTestbed
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitA
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitB
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitC
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitD
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitE
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitF
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitG
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitH
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitI
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.mergeEnglishIntoSpanish
import io.redgreen.timelapse.vcs.ChangedFile.Addition
import io.redgreen.timelapse.vcs.ChangedFile.Deletion
import io.redgreen.timelapse.vcs.ChangedFile.Modification
import io.redgreen.timelapse.vcs.ChangedFile.Rename
import org.junit.jupiter.api.Test

class GitFunctionsTest {
  private val repository = GitTestbed.repository

  @Test
  fun `it should get a list of files from initial commit`() {
    // given
    val initialCommitId = exhibitA // exhibit a: add three new files

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
    val newFileCommitId = exhibitB // exhibit b: add a new file

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
    val modifiedFileCommitId = exhibitC // exhibit c: modify a file

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
    val deletedFileCommitId = exhibitD // exhibit d: delete a file

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
    val renamedFileCommitId = exhibitF // exhibit f: rename a file

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
    val commitId = exhibitG // exhibit g: renames, deletion, addition and modification

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
    val newlyAddedFileCommitId = exhibitE // exhibit e: copy a file

    // when
    val newlyAddedFileDiff = repository.getBlobDiff(newlyAddedFileCommitId, "file-1-copy.txt") as Simple

    // then
    assertThat(newlyAddedFileDiff.rawDiff)
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
    val deletedFileCommitId = exhibitG // exhibit g: renames, deletion, addition and modification

    // when
    val deletedFileDiff = repository.getBlobDiff(deletedFileCommitId, "file-4.txt") as Simple

    // then
    assertThat(deletedFileDiff.rawDiff)
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
    val modifiedFileCommitId = exhibitG // exhibit g: renames, deletion, addition and modification

    // when
    val modifiedFileDiff = repository.getBlobDiff(modifiedFileCommitId, "file-1.txt") as Simple

    // then
    assertThat(modifiedFileDiff.rawDiff)
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
    val renamedFileCommitId = exhibitF // exhibit f: rename a file

    // when
    val renamedFileDiff = repository.getBlobDiff(renamedFileCommitId, "file-4.txt") as Simple

    // then
    assertThat(renamedFileDiff.rawDiff)
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
    val initialCommit = exhibitA // exhibit a: add three new files

    // when
    val newFileDiff = repository.getBlobDiff(initialCommit, "file-1.txt") as Simple

    // then
    assertThat(newFileDiff.rawDiff)
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
    val deletedFileCommitId = exhibitG // exhibit g: renames, deletion, addition and modification

    // when
    val deletedEmptyFileDiff = repository.getBlobDiff(deletedFileCommitId, "file-3.txt") as Simple

    // then
    assertThat(deletedEmptyFileDiff.rawDiff)
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
    val newEmptyFileCommitId = exhibitG // exhibit g: renames, deletion, addition and modification

    // when
    val newEmptyFileDiff = repository.getBlobDiff(newEmptyFileCommitId, "file-b.txt") as Simple

    // then
    assertThat(newEmptyFileDiff.rawDiff)
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

  @Test
  fun `it should get the diff for a merge commit`() {
    // given
    val mergeCommitId = mergeEnglishIntoSpanish // Merge branch 'english' into spanish

    // when
    val mergeCommitDiff = repository.getBlobDiff(mergeCommitId, "file-1.txt") as Merge

    // then
    assertThat(mergeCommitDiff.diffs)
      .hasSize(2)

    // then - parent 1
    val parent1Diff = mergeCommitDiff.diffs[0]
    assertThat(parent1Diff.parentCommitId)
      .isEqualTo(exhibitI) // exhibit i: pre-merge modification (Spanish)
    assertThat(parent1Diff.rawDiff)
      .isEqualTo(
        """
          diff --git a/file-1.txt b/file-1.txt
          index 265d673..f17d600 100644
          --- a/file-1.txt
          +++ b/file-1.txt
          @@ -1 +1 @@
          -Hola, mundo!
          +Hola, world!
          
        """.trimIndent()
      )

    // then - parent 2
    val parent2Diff = mergeCommitDiff.diffs[1]
    assertThat(parent2Diff.parentCommitId)
      .isEqualTo(exhibitH) // exhibit h: pre-merge modification (English)
    assertThat(parent2Diff.rawDiff)
      .isEqualTo(
        """
          diff --git a/file-1.txt b/file-1.txt
          index af5626b..f17d600 100644
          --- a/file-1.txt
          +++ b/file-1.txt
          @@ -1 +1 @@
          -Hello, world!
          +Hola, world!
          
        """.trimIndent()
      )
  }

  @Test
  fun `it should list all remote branches`() {
    // when
    val remoteBranches = repository.listRemoteBranches()

    // then
    assertThat(remoteBranches)
      .containsAtLeast(
        Branch("refs/remotes/origin/HEAD", "2c132dd9e3e32b6493e7d8c8ad595ea40b54a278"),
        Branch("refs/remotes/origin/branch-a", "68958540148efb4dd0dbfbb181df330deaffbe13"),
        Branch("refs/remotes/origin/branch-b", "2c132dd9e3e32b6493e7d8c8ad595ea40b54a278"),
        Branch("refs/remotes/origin/group-a/branch-a", "374bbc8b4cefbb6c37feb5526a68f5d7bf0aeb7f"),
        Branch("refs/remotes/origin/master", "2c132dd9e3e32b6493e7d8c8ad595ea40b54a278"),
      )
  }
}
