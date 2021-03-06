package io.redgreen.timelapse.affectedfiles.usecases

import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Added
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Deleted
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Modified
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Moved
import io.redgreen.timelapse.core.CommitHash
import io.redgreen.timelapse.core.GitDirectory
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
    val testObserver = useCase
      .invoke(gitTestbedGitDirectory, CommitHash(descendent), CommitHash(ancestor))
      .test()

    // then
    val affectedFiles = listOf(
      Added(FILE_1_TXT, 0),
      Added(FILE_2_TXT, 0),
      Added(FILE_3_TXT, 0),
    )
    testObserver
      .assertValue(affectedFiles)
      .assertNoErrors()
      .assertComplete()
  }

  @Test
  fun `it should get changed files from a non-initial commit`() {
    // given
    val descendent = CommitHash(exhibitG) // exhibit g: renames, deletion, addition and modification
    val ancestor = CommitHash(exhibitF) // exhibit f: rename a file

    // when
    val testObserver = useCase
      .invoke(gitTestbedGitDirectory, descendent, ancestor)
      .test()

    // then
    val changedFiles = listOf(
      Modified("file-1.txt", 1, 1),
      Deleted("file-3.txt", 0),
      Deleted("file-4.txt", 1),
      Moved("file-a.txt", "file-2.txt", 0, 0),
      Added("file-b.txt", 0),
      Added("file-c.txt", 1),
    )
    testObserver
      .assertValue(changedFiles)
      .assertNoErrors()
      .assertComplete()
  }

  @Test
  fun `it should return an error for an invalid initial descendent commit hash`() {
    // given
    val invalidInitialCommitHash = CommitHash(INVALID_COMMIT_ID)

    // when
    val testObserver = useCase
      .invoke(gitTestbedGitDirectory, invalidInitialCommitHash, invalidInitialCommitHash)
      .test()

    // then
    testObserver
      .assertNoValues()
      .assertError { throwable ->
        throwable is IllegalArgumentException
            && throwable.message == "Invalid descendent commit ID: ${invalidInitialCommitHash.value}"
      }
  }

  @Test
  fun `it should return an error for an invalid non-initial descendent commit hash`() {
    // given
    val invalidDescendentHash = CommitHash(INVALID_COMMIT_ID)
    val ancestorHash = CommitHash(exhibitD) // exhibit d: delete a file

    // when
    val testObserver = useCase
      .invoke(gitTestbedGitDirectory, invalidDescendentHash, ancestorHash)
      .test()

    // then
    testObserver
      .assertNoValues()
      .assertError { throwable ->
        throwable is IllegalArgumentException
            && throwable.message == "Invalid descendent commit ID: ${invalidDescendentHash.value}"
      }
  }

  @Test
  fun `it should return an error for an invalid non-initial ancestor commit hash`() {
    // given
    val descendentHash = CommitHash(exhibitD) // exhibit d: delete a file
    val invalidAncestorHash = CommitHash(INVALID_COMMIT_ID)

    // when
    val testObserver = useCase
      .invoke(gitTestbedGitDirectory, descendentHash, invalidAncestorHash)
      .test()

    // then
    testObserver
      .assertNoValues()
      .assertError { throwable ->
        throwable is IllegalArgumentException
            && throwable.message == "Invalid ancestor commit ID: ${invalidAncestorHash.value}"
      }
  }

  @Test
  fun `it should return a list of affected files for commits that are far apart`() {
    // given
    val descendentCommitHash = CommitHash(exhibitG)
    val ancestorCommitHash = CommitHash(exhibitA)

    // when
    val testObserver = useCase
      .invoke(gitTestbedGitDirectory, descendentCommitHash, ancestorCommitHash)
      .test()

    // then
    val affectedFiles = listOf(
      Modified("file-1.txt", 0, 1),
      Deleted("file-3.txt", 0),
      Moved("file-a.txt", "file-2.txt", 0, 0),
      Added("file-b.txt", 0),
      Added("file-c.txt", 1)
    )
    testObserver
      .assertNoErrors()
      .assertValue(affectedFiles)
  }
}
