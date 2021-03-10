package liftoff.affectedfiles.props.view

import io.redgreen.liftoff.javafx.components.DiscoverGitReposComboBox.GitRepo
import io.redgreen.timelapse.core.TrackedFilePath
import liftoff.affectedfiles.model.AffectingCommit

interface AffectedFilesPropsUiView {
  fun populateGitRepos(gitRepos: List<GitRepo>)
  fun populateTrackedFiles(trackedFilePaths: List<TrackedFilePath>)
  fun populateAffectingCommits(affectingCommits: List<AffectingCommit>)
  fun clearAffectingCommitsList()
  fun clearAffectedFileCallbackText()
}
