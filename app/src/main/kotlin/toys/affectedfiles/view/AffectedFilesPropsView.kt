package toys.affectedfiles.view

import io.redgreen.liftoff.javafx.components.DiscoverGitReposComboBox.GitRepo
import io.redgreen.timelapse.core.TrackedFilePath
import toys.affectedfiles.model.AffectingCommit

interface AffectedFilesPropsView {
  fun populateGitRepos(gitRepos: List<GitRepo>)
  fun populateTrackedFiles(trackedFilePaths: List<TrackedFilePath>)
  fun populateAffectingCommits(affectingCommits: List<AffectingCommit>)
  fun clearAffectedFileCallbackText()
}
