package io.redgreen.timelapse.changedfiles

import com.spotify.mobius.Next
import com.spotify.mobius.Next.next
import com.spotify.mobius.Update
import io.redgreen.timelapse.changedfiles.ChangedFilesModel.HasSelection
import io.redgreen.timelapse.changedfiles.ChangedFilesModel.NoSelection

object ChangedFilesUpdate : Update<ChangedFilesModel, ChangedFilesEvent, ChangedFilesEffect> {
  override fun update(
    model: ChangedFilesModel,
    event: ChangedFilesEvent
  ): Next<ChangedFilesModel, ChangedFilesEffect> {
    return when (event) {
      is RevisionSelected -> next(
        (model as NoSelection).revisionSelected(event.commitId, event.filePath),
        setOf(FetchChangedFiles(event.commitId, event.filePath))
      )
      NoOtherFilesChanged -> next((model as HasSelection).noOtherFilesChanged())
      is SomeFilesChanged -> next((model as HasSelection).someFilesChanged(event.filePaths))
      UnableToFetchChangedFiles -> next((model as HasSelection).unableToFetchChangedFiles())
    }
  }
}
