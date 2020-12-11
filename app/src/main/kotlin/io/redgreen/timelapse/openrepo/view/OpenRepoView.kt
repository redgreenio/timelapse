package io.redgreen.timelapse.openrepo.view

interface OpenRepoView {
  fun displayFileChooser()
  fun openGitRepository(path: String)
  fun showNotAGitRepositoryError(path: String)
}
