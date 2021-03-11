package liftoff.affectedfiles.props.view

import io.redgreen.timelapse.git.model.GitDirectory
import io.redgreen.timelapse.git.model.TrackedFilePath
import liftoff.affectedfiles.model.AffectingCommit

interface AffectedFilesPropsUiView {
  fun populateGitDirectories(gitDirectories: List<GitDirectory>)

  fun populateTrackedFiles(trackedFilePaths: List<TrackedFilePath>)
  fun clearTrackedFiles()

  fun populateAffectingCommits(affectingCommits: List<AffectingCommit>)
  fun clearAffectingCommits()

  fun clearAffectedFileCallbackText()
}
