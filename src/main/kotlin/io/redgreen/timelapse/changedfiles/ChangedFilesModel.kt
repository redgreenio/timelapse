package io.redgreen.timelapse.changedfiles

sealed class ChangedFilesModel {
  object NoSelection : ChangedFilesModel() {
    fun revisionSelected(
      commitId: String,
      filePath: String
    ): ChangedFilesModel =
      HasSelection(commitId, filePath, emptyList())
  }

  data class HasSelection(
    val commitId: String,
    val filePath: String,
    val changedFiles: List<String>
  ) : ChangedFilesModel() {
    fun noOtherFilesChanged(): HasSelection =
      copy(changedFiles = emptyList())
  }
}
