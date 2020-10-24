package io.redgreen.timelapse.changedfiles.contracts

import io.redgreen.timelapse.TimelapseApp
import io.redgreen.timelapse.contracts.FulfilledBy
import io.redgreen.timelapse.vcs.ChangedFile

@FulfilledBy(TimelapseApp::class)
interface ReadingAreaContract {
  fun showChangedFileDiff(commitId: String, changedFile: ChangedFile)
}
