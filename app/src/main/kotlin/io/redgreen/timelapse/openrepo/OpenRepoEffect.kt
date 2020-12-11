package io.redgreen.timelapse.openrepo

sealed class OpenRepoEffect

object FindGitUsername : OpenRepoEffect()

object DisplayFileChooser : OpenRepoEffect()

data class DetectGitRepository(
  val path: String
) : OpenRepoEffect()

data class UpdateRecentRepositories(
  val path: String
) : OpenRepoEffect()

data class OpenGitRepository(
  val path: String
) : OpenRepoEffect()

object GetRecentRepositories : OpenRepoEffect() {
  sealed class Failure {
    object Unknown : Failure()
  }
}

data class ShowNotAGitRepositoryError(
  val path: String
) : OpenRepoEffect()
