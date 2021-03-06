package io.redgreen.timelapse.affectedfiles.usecases

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Added
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Deleted
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Modified
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Moved
import io.redgreen.timelapse.core.CommitHash
import io.redgreen.timelapse.core.GitDirectory
import io.redgreen.timelapse.core.TrackedFilePath
import io.redgreen.timelapse.fixtures.FixtureRepository.Companion.INVALID_COMMIT_ID
import io.redgreen.timelapse.fixtures.GitTestbed
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitA
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitD
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitF
import io.redgreen.timelapse.fixtures.GitTestbed.Commit.exhibitG
import io.redgreen.timelapse.fixtures.GitTestbed.Content.FILE_1_TXT
import io.redgreen.timelapse.fixtures.GitTestbed.Content.FILE_2_TXT
import io.redgreen.timelapse.fixtures.GitTestbed.Content.FILE_3_TXT
import org.junit.jupiter.api.Test

internal class GetAffectedFilesUseCaseTest {
  private val useCase = GetAffectedFilesUseCase()
  private val gitTestbedGitDirectory = GitDirectory.from("${GitTestbed.path.absolutePath}/.git").get()

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
    val actualAffectedFiles = (either as Either.Right).b
    assertThat(actualAffectedFiles)
      .containsExactly(
        Added(TrackedFilePath(FILE_1_TXT), 0),
        Added(TrackedFilePath(FILE_2_TXT), 0),
        Added(TrackedFilePath(FILE_3_TXT), 0),
      )
      .inOrder()
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
    val actualAffectedFiles = (either as Either.Right).b
    assertThat(actualAffectedFiles)
      .containsExactly(
        Modified(TrackedFilePath("file-1.txt"), 1, 1),
        Deleted(TrackedFilePath("file-3.txt"), 0),
        Deleted(TrackedFilePath("file-4.txt"), 1),
        Moved(TrackedFilePath("file-a.txt"), TrackedFilePath("file-2.txt"), 0, 0),
        Added(TrackedFilePath("file-b.txt"), 0),
        Added(TrackedFilePath("file-c.txt"), 1),
      )
      .inOrder()
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
    val actualAffectedFiles = (either as Either.Right).b
    assertThat(actualAffectedFiles)
      .containsExactly(
        Modified(TrackedFilePath("file-1.txt"), 0, 1),
        Deleted(TrackedFilePath("file-3.txt"), 0),
        Moved(TrackedFilePath("file-a.txt"), TrackedFilePath("file-2.txt"), 0, 0),
        Added(TrackedFilePath("file-b.txt"), 0),
        Added(TrackedFilePath("file-c.txt"), 1),
      )
      .inOrder()
  }
}
