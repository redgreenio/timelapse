package io.redgreen.timelapse.vcs.git

import io.redgreen.timelapse.domain.openGitRepository
import io.redgreen.timelapse.vcs.ChangedFile.Addition
import io.redgreen.timelapse.vcs.ChangedFile.Deletion
import io.redgreen.timelapse.vcs.ChangedFile.Modification
import io.redgreen.timelapse.vcs.ChangedFile.Rename
import org.junit.jupiter.api.Test
import java.io.File

class GitRepositoryServiceTest {
  private val gitRepository = openGitRepository(File("git-testbed"))
  private val repositoryService = GitRepositoryService(gitRepository)

  @Test
  fun `it should get changed files from an initial commit`() {
    // given
    val initialCommit = "b6748190194e697df97d3dd9801af4f55d763ef9" // exhibit a: add three new files

    // when
    val testObserver = repositoryService.getChangedFiles(initialCommit).test()

    // then
    val changedFiles = listOf("file-1.txt", "file-2.txt", "file-3.txt").map(::Addition)
    testObserver
      .assertValue(changedFiles)
      .assertNoErrors()
      .assertComplete()
  }

  @Test
  fun `it should get changed files from a non-initial commit`() {
    // given
    val nonInitialCommit = "374bbc8b4cefbb6c37feb5526a68f5d7bf0aeb7f" // exhibit g: renames, deletion, addition and modification

    // when
    val testObserver = repositoryService.getChangedFiles(nonInitialCommit).test()

    // then
    val changedFiles = listOf(
      Modification("file-1.txt"),
      Deletion("file-3.txt"),
      Deletion("file-4.txt"),
      Addition("file-b.txt"),
      Addition("file-c.txt"),
      Rename("file-a.txt", "file-2.txt")
    )
    testObserver
      .assertValue(changedFiles)
      .assertNoErrors()
      .assertComplete()
  }

  @Test
  fun `it should return an error for an invalid commit`() {
    // given
    val invalidCommitId = "invalid-commit-id"

    // when
    val testObserver = repositoryService.getChangedFiles(invalidCommitId).test()

    // then
    testObserver
      .assertNoValues()
      .assertError { throwable ->
        throwable is IllegalArgumentException && throwable.message == ("Invalid commit ID: $invalidCommitId")
      }
  }
}
