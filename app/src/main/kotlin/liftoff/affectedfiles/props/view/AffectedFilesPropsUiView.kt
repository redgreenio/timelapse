package liftoff.affectedfiles.props.view

import io.redgreen.liftoff.javafx.components.DiscoverGitReposComboBox.GitRepo
import io.redgreen.timelapse.git.model.TrackedFilePath
import liftoff.affectedfiles.model.AffectingCommit

interface AffectedFilesPropsUiView {
  fun populateGitRepos(gitRepos: List<GitRepo>)

  fun populateTrackedFiles(trackedFilePaths: List<TrackedFilePath>)
  fun clearTrackedFiles()

  fun populateAffectingCommits(affectingCommits: List<AffectingCommit>)
  fun clearAffectingCommits()

  fun clearAffectedFileCallbackText()
}
