package io.redgreen.timelapse.changedfiles

import com.spotify.mobius.First
import com.spotify.mobius.First.first
import com.spotify.mobius.Init
import io.redgreen.timelapse.changedfiles.ChangedFilesModel.NoSelection

class ChangedFilesInit : Init<ChangedFilesModel, ChangedFilesEffect> {
  override fun init(model: ChangedFilesModel): First<ChangedFilesModel, ChangedFilesEffect> =
    first(NoSelection)
}
