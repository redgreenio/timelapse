package io.redgreen.timelapse.changedfiles.contracts

import io.redgreen.timelapse.TimelapseApp
import io.redgreen.timelapse.contracts.FulfilledBy

@FulfilledBy(TimelapseApp::class)
interface ReadingAreaContract {
  fun showDiff(commitId: String, filePath: String)
}
