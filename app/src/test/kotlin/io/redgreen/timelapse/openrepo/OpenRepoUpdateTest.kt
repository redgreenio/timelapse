package io.redgreen.timelapse.openrepo

import com.spotify.mobius.test.NextMatchers.hasEffects
import com.spotify.mobius.test.NextMatchers.hasModel
import com.spotify.mobius.test.NextMatchers.hasNoEffects
import com.spotify.mobius.test.NextMatchers.hasNoModel
import com.spotify.mobius.test.UpdateSpec.assertThatNext
import io.redgreen.architecture.mobius.spec
import io.redgreen.timelapse.openrepo.data.RecentGitRepository
import org.junit.jupiter.api.Test

class OpenRepoUpdateTest {
  private val withUpdateSpec = OpenRepoUpdate.spec()
  private val gitUsernameFound = OpenRepoModel
    .start()
    .gitUsernameFound("Ajay")

  @Test
  fun `when a git username is not found in the global config, then show a generic greeting`() {
    val start = OpenRepoModel.start()

    withUpdateSpec
      .given(start)
      .whenEvent(GitUsernameNotFound)
      .then(
        assertThatNext(
          hasModel(start.cannotFindGitUsername()),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when a git username is found, then show a personalized greeting`() {
    val start = OpenRepoModel.start()
    val username = "Ajay"

    withUpdateSpec
      .given(start)
      .whenEvent(GitUsernameFound(username))
      .then(
        assertThatNext(
          hasModel(start.gitUsernameFound(username)),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when the user clicks on open repository, then open the file chooser dialog`() {
    withUpdateSpec
      .given(gitUsernameFound)
      .whenEvent(ChooseGitRepository)
      .then(
        assertThatNext(
          hasNoModel(),
          hasEffects(DisplayFileChooser)
        )
      )
  }

  @Test
  fun `when the user chooses a repo directory, then attempt to open the repo`() {
    val repositoryPath = "~/Users/ajay/GitHubProjects/angular"

    withUpdateSpec
      .given(gitUsernameFound)
      .whenEvent(GitRepositoryChosen(repositoryPath))
      .then(
        assertThatNext(
          hasNoModel(),
          hasEffects(DetectGitRepository(repositoryPath))
        )
      )
  }

  @Test
  fun `when the chosen path contains a repository, then open the repository`() {
    val repositoryPath = "~/Users/ajay/GitHubProjects/angular"

    withUpdateSpec
      .given(gitUsernameFound)
      .whenEvent(GitRepositoryDetected(repositoryPath))
      .then(
        assertThatNext(
          hasNoModel(),
          hasEffects(OpenGitRepository(repositoryPath))
        )
      )
  }

  @Test
  fun `when the chosen path does not contain a git repository, then show an error message`() {
    val path = "non/existent/path"

    withUpdateSpec
      .given(gitUsernameFound)
      .whenEvent(GitRepositoryNotDetected(path))
      .then(
        assertThatNext(
          hasNoModel(),
          hasEffects(ShowNotAGitRepositoryError(path))
        )
      )
  }

  @Test
  fun `when there are no recent repositories, show no recent repositories`() {
    withUpdateSpec
      .given(gitUsernameFound)
      .whenEvent(NoRecentRepositories)
      .then(
        assertThatNext(
          hasModel(gitUsernameFound.noRecentRepositories()),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when there are recent repositories, list recent repositories`() {
    val recentRepositories = listOf(
      "~/Users/ajay/GitHubProjects/angular/.git",
      "~/Users/ajay/GitHubProjects/JGit/.git"
    )
      .map(::RecentGitRepository)

    withUpdateSpec
      .given(gitUsernameFound)
      .whenEvent(HasRecentRepositories(recentRepositories))
      .then(
        assertThatNext(
          hasModel(gitUsernameFound.hasRecentRepositories(recentRepositories)),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when there was an error getting recent repositories, then show no recent repositories`() {
    withUpdateSpec
      .given(gitUsernameFound)
      .whenEvent(UnableToGetRecentRepositories)
      .then(
        assertThatNext(
          hasModel(gitUsernameFound.failedToGetRecentRepositories()),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when the user selects a recent repository, then open the repository`() {
    val retrofit = "/Users/ajay/IdeaProjects/retrofit/.git"
    val angular = "~/GitHubProjects/angular/.git"
    val recentRepositories = listOf(retrofit, angular).map(::RecentGitRepository)

    val hasRecentRepositories = gitUsernameFound
      .hasRecentRepositories(recentRepositories)

    withUpdateSpec
      .given(hasRecentRepositories)
      .whenEvent(OpenRecentRepository(1))
      .then(
        assertThatNext(
          hasNoModel(),
          hasEffects(OpenGitRepository(angular))
        )
      )
  }
}
