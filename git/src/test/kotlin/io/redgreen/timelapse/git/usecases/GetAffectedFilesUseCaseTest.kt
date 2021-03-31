package io.redgreen.timelapse.git.usecases

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.fixtures.GitFixture.Companion.INVALID_COMMIT_ID
import io.redgreen.timelapse.fixtures.GitTestbed
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitA
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitB
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitC
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitD
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitE
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitF
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitG
import io.redgreen.timelapse.fixtures.GitTestbed.Content.FILE_1_COPY_TXT
import io.redgreen.timelapse.fixtures.GitTestbed.Content.FILE_1_TXT
import io.redgreen.timelapse.fixtures.GitTestbed.Content.FILE_2_TXT
import io.redgreen.timelapse.fixtures.GitTestbed.Content.FILE_3_TXT
import io.redgreen.timelapse.fixtures.GitTestbed.Content.FILE_4_TXT
import io.redgreen.timelapse.fixtures.GitTestbed.Content.FILE_A_TXT
import io.redgreen.timelapse.fixtures.GitTestbed.Content.FILE_B_TXT
import io.redgreen.timelapse.fixtures.GitTestbed.Content.FILE_C_TXT
import io.redgreen.timelapse.git.model.AffectedFile.Added
import io.redgreen.timelapse.git.model.AffectedFile.Deleted
import io.redgreen.timelapse.git.model.AffectedFile.Modified
import io.redgreen.timelapse.git.model.AffectedFile.Moved
import io.redgreen.timelapse.git.model.CommitHash
import io.redgreen.timelapse.git.model.GitDirectory
import io.redgreen.timelapse.git.model.TrackedFilePath
import org.junit.jupiter.api.Test

internal class GetAffectedFilesUseCaseTest {
  private val useCase = GetAffectedFilesUseCase()
  private val gitTestbedGitDirectory = GitDirectory.from(GitTestbed.path.absolutePath).get()

  @Test
  fun `it should get changed files from an initial commit`() {
    // given
    val descendent = exhibitA // exhibit a: add three new files

    @Suppress("UnnecessaryVariable")
    val ancestor = descendent // because, initial commit is its own parent

    // when
    val either = useCase
      .invoke(gitTestbedGitDirectory, CommitHash(descendent), CommitHash(ancestor))

    // then
    val actualAffectedFiles = (either as Either.Right).value
    assertThat(actualAffectedFiles)
      .containsExactly(
        Added(TrackedFilePath(FILE_1_TXT), 0),
        Added(TrackedFilePath(FILE_2_TXT), 0),
        Added(TrackedFilePath(FILE_3_TXT), 0),
      )
  }

  @Test
  fun `it should get changed files from a non-initial commit`() {
    // given
    val descendent = CommitHash(exhibitG) // exhibit g: renames, deletion, addition and modification
    val ancestor = CommitHash(exhibitF) // exhibit f: rename a file

    // when
    val either = useCase
      .invoke(gitTestbedGitDirectory, descendent, ancestor)

    // then
    val actualAffectedFiles = (either as Either.Right).value
    assertThat(actualAffectedFiles)
      .containsExactly(
        Modified(TrackedFilePath(FILE_1_TXT), 1, 1),
        Deleted(TrackedFilePath(FILE_3_TXT), 0),
        Deleted(TrackedFilePath(FILE_4_TXT), 1),
        Moved(TrackedFilePath(FILE_A_TXT), TrackedFilePath(FILE_2_TXT), 0, 0),
        Added(TrackedFilePath(FILE_B_TXT), 0),
        Added(TrackedFilePath(FILE_C_TXT), 1),
      )
  }

  @Test
  fun `it should return an error for an invalid initial descendent commit hash`() {
    // given
    val invalidInitialCommitHash = CommitHash(INVALID_COMMIT_ID)

    // when
    val either = useCase
      .invoke(gitTestbedGitDirectory, invalidInitialCommitHash, invalidInitialCommitHash)

    // then
    val actualThrowable = (either as Either.Left).a
    assertThat(actualThrowable)
      .isInstanceOf(IllegalArgumentException::class.java)
    assertThat(actualThrowable.message)
      .isEqualTo("Invalid descendent commit ID: ${invalidInitialCommitHash.value}")
  }

  @Test
  fun `it should return an error for an invalid non-initial descendent commit hash`() {
    // given
    val invalidDescendentHash = CommitHash(INVALID_COMMIT_ID)
    val ancestorHash = CommitHash(exhibitD) // exhibit d: delete a file

    // when
    val either = useCase
      .invoke(gitTestbedGitDirectory, invalidDescendentHash, ancestorHash)

    // then
    val actualThrowable = (either as Either.Left).a
    assertThat(actualThrowable)
      .isInstanceOf(IllegalArgumentException::class.java)
    assertThat(actualThrowable.message)
      .isEqualTo("Invalid descendent commit ID: ${invalidDescendentHash.value}")
  }

  @Test
  fun `it should return an error for an invalid non-initial ancestor commit hash`() {
    // given
    val descendentHash = CommitHash(exhibitD) // exhibit d: delete a file
    val invalidAncestorHash = CommitHash(INVALID_COMMIT_ID)

    // when
    val either = useCase
      .invoke(gitTestbedGitDirectory, descendentHash, invalidAncestorHash)

    // then
    val actualThrowable = (either as Either.Left).a
    assertThat(actualThrowable)
      .isInstanceOf(IllegalArgumentException::class.java)
    assertThat(actualThrowable.message)
      .isEqualTo("Invalid ancestor commit ID: ${invalidAncestorHash.value}")
  }

  @Test
  fun `it should return a list of affected files for commits that are far apart`() {
    // given
    val descendentCommitHash = CommitHash(exhibitG)
    val ancestorCommitHash = CommitHash(exhibitA)

    // when
    val either = useCase
      .invoke(gitTestbedGitDirectory, descendentCommitHash, ancestorCommitHash)

    // then
    val actualAffectedFiles = (either as Either.Right).value
    assertThat(actualAffectedFiles)
      .containsExactly(
        Modified(TrackedFilePath(FILE_1_TXT), 0, 1),
        Deleted(TrackedFilePath(FILE_3_TXT), 0),
        Moved(TrackedFilePath(FILE_A_TXT), TrackedFilePath(FILE_2_TXT), 0, 0),
        Added(TrackedFilePath(FILE_B_TXT), 0),
        Added(TrackedFilePath(FILE_C_TXT), 1),
      )
  }

  @Test
  fun `it should get added file from a non-initial commit`() {
    // given
    val descendentCommitHash = CommitHash(exhibitB) // exhibit b: add a new file
    val ancestorCommitHash = CommitHash(exhibitA) // exhibit a: add three new files

    // when
    val either = useCase
      .invoke(gitTestbedGitDirectory, descendentCommitHash, ancestorCommitHash)

    // then
    val actualAffectedFiles = (either as Either.Right).value
    assertThat(actualAffectedFiles)
      .containsExactly(
        Added(TrackedFilePath(FILE_4_TXT), 0)
      )
  }

  @Test
  fun `it should get modified file from a non-initial commit`() {
    // given
    val descendentCommitHash = CommitHash(exhibitC) // exhibit c: modify a file
    val ancestorCommitHash = CommitHash(exhibitB) // exhibit b: add a new file

    // when
    val either = useCase
      .invoke(gitTestbedGitDirectory, descendentCommitHash, ancestorCommitHash)

    // then
    val actualAffectedFiles = (either as Either.Right).value
    assertThat(actualAffectedFiles)
      .containsExactly(
        Modified(TrackedFilePath(FILE_1_TXT), 0, 1)
      )
  }

  @Test
  fun `it should get a deleted file from a non-initial commit`() {
    // given
    val descendentCommitHash = CommitHash(exhibitD) // exhibit d: delete a file
    val ancestorCommitHash = CommitHash(exhibitC) // exhibit c: modify a file

    // when
    val either = useCase
      .invoke(gitTestbedGitDirectory, descendentCommitHash, ancestorCommitHash)

    // then
    val actualAffectedFiles = (either as Either.Right).value
    assertThat(actualAffectedFiles)
      .containsExactly(
        Deleted(TrackedFilePath(FILE_4_TXT), 0)
      )
  }

  @Test
  fun `it should get a renamed file from a non-initial commit`() {
    // given
    val descendentCommitHash = CommitHash(exhibitF) // exhibit f: rename a file
    val ancestorCommitHash = CommitHash(exhibitE) // exhibit e: copy a file

    // when
    val either = useCase
      .invoke(gitTestbedGitDirectory, descendentCommitHash, ancestorCommitHash)

    // then
    val actualAffectedFiles = (either as Either.Right).value
    assertThat(actualAffectedFiles)
      .containsExactly(
        Moved(TrackedFilePath(FILE_4_TXT), TrackedFilePath(FILE_1_COPY_TXT), 0, 0)
      )
  }
}
