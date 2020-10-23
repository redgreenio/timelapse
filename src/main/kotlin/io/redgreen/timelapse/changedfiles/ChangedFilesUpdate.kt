package io.redgreen.timelapse.changedfiles

import com.spotify.mobius.Next
import com.spotify.mobius.Next.dispatch
import com.spotify.mobius.Next.next
import com.spotify.mobius.Update
import io.redgreen.timelapse.mobius.AsyncOp.Content

object ChangedFilesUpdate : Update<ChangedFilesModel, ChangedFilesEvent, ChangedFilesEffect> {
  override fun update(
    model: ChangedFilesModel,
    event: ChangedFilesEvent
  ): Next<ChangedFilesModel, ChangedFilesEffect> {
    return when (event) {
      is FileAndRevisionSelected -> next(
        model.fileAndRevisionSelected(event.filePath, event.commitId),
        setOf(GetChangedFiles(event.commitId, event.filePath))
      )

      NoOtherFilesChanged -> next(model.noOtherFilesChanged())

      is SomeMoreFilesChanged -> next(model.someMoreFilesChanged(event.filePaths))

      GettingChangedFilesFailed -> next(model.gettingChangedFilesFailed())

      RetryGettingChangedFiles -> next(model.retryGettingChangedFiles())

      is ChangedFileSelected -> {
        val (commitId, filePath) = getCommitIdAndFilePath(model, event.index)
        dispatch(setOf(ShowDiff(commitId, filePath)))
      }
    }
  }

  private fun getCommitIdAndFilePath(
    model: ChangedFilesModel,
    index: Int
  ): Pair<String, String> {
    val commitId = model.commitId!!
    val filePath = (model.getChangedFilesAsyncOp as Content<ChangedFiles>).content[index]
    return commitId to filePath
  }
}
