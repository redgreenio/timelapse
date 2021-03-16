package affectedfiles.props.view

import affectedfiles.model.AffectingCommit
import io.redgreen.timelapse.git.model.GitDirectory
import io.redgreen.timelapse.git.model.TrackedFilePath

interface AffectedFilesPropsUiView {
  fun populateGitDirectories(gitDirectories: List<GitDirectory>)

  fun populateTrackedFiles(trackedFilePaths: List<TrackedFilePath>)
  fun clearTrackedFiles()

  fun populateAffectingCommits(affectingCommits: List<AffectingCommit>)
  fun clearAffectingCommits()

  fun clearAffectedFileCallbackText()
}
