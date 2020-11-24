package io.redgreen.timelapse.changedfiles.contracts

import io.redgreen.timelapse.vcs.ChangedFile

interface ReadingAreaContract {
  fun showChangedFileDiff(commitId: String, changedFile: ChangedFile)
}
