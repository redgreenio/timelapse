package io.redgreen.timelapse.changedfiles

import com.spotify.mobius.Next
import com.spotify.mobius.Next.dispatch
import com.spotify.mobius.Next.next
import com.spotify.mobius.Update
import io.redgreen.architecture.mobius.AsyncOp.Content
import io.redgreen.timelapse.vcs.ChangedFile

object ChangedFilesUpdate : Update<ChangedFilesModel, ChangedFilesEvent, ChangedFilesEffect> {
  override fun update(
    model: ChangedFilesModel,
    event: ChangedFilesEvent
  ): Next<ChangedFilesModel, ChangedFilesEffect> = when (event) {
    is FileAndRevisionSelected -> next(
      model.fileAndRevisionSelected(event.selectedFilePath, event.commitId),
      setOf(GetChangedFiles(event.commitId, event.selectedFilePath))
    )

    NoOtherFilesChanged -> next(model.noOtherFilesChanged())

    is SomeMoreFilesChanged -> next(model.someMoreFilesChanged(event.changedFiles))

    GettingChangedFilesFailed -> next(model.gettingChangedFilesFailed())

    RetryGettingChangedFiles -> next(model.retryGettingChangedFiles())

    is ChangedFileSelected -> {
      val (commitId, changedFile) = getCommitIdAndFilePath(model, event.index)
      dispatch(setOf(ShowDiff(commitId, changedFile)))
    }
  }

  private fun getCommitIdAndFilePath(
    model: ChangedFilesModel,
    index: Int
  ): Pair<String, ChangedFile> {
    val commitId = model.commitId!!
    val filePath = (model.getChangedFilesAsyncOp as Content<List<ChangedFile>>).content[index]
    return commitId to filePath
  }
}
