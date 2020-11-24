package io.redgreen.timelapse.changedfiles

import com.spotify.mobius.First
import com.spotify.mobius.First.first
import com.spotify.mobius.Init

object ChangedFilesInit : Init<ChangedFilesModel, ChangedFilesEffect> {
  override fun init(model: ChangedFilesModel): First<ChangedFilesModel, ChangedFilesEffect> =
    first(ChangedFilesModel.noFileAndRevisionSelected())
}
