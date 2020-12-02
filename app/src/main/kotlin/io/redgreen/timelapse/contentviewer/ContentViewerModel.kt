package io.redgreen.timelapse.contentviewer

import io.redgreen.timelapse.mobius.AsyncOp
import io.redgreen.timelapse.mobius.AsyncOp.Companion.idle
import io.redgreen.timelapse.mobius.AsyncOp.Companion.inFlight

data class ContentViewerModel(
  val selectedFilePath: String?,
  val commitId: String?,
  val loadBlogDiffInformationAsyncOp: AsyncOp<Any, Any> = idle(),
  val loadBlogDiffAsyncOp: AsyncOp<Any, Any> = idle()
) {
  companion object {
    fun noFileAndRevisionSelected(): ContentViewerModel {
      return ContentViewerModel(null, null)
    }
  }

  fun fileAndRevisionSelected(
    selectedFilePath: String,
    commitId: String
  ): ContentViewerModel {
    return this.copy(
      selectedFilePath = selectedFilePath,
      commitId = commitId,
      loadBlogDiffInformationAsyncOp = inFlight(),
      loadBlogDiffAsyncOp = inFlight()
    )
  }
}
