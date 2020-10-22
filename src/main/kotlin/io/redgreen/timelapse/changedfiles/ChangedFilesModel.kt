package io.redgreen.timelapse.changedfiles

sealed class ChangedFilesModel {
  object NoSelection : ChangedFilesModel() {
    fun revisionSelected(
      commitId: String,
      filePath: String,
    ): ChangedFilesModel =
      HasSelection(commitId, filePath, emptyList())
  }

  data class HasSelection(
    val commitId: String,
    val filePath: String,
    val changedFilePaths: List<String>,
    val isError: Boolean = false,
  ) : ChangedFilesModel() {
    fun noOtherFilesChanged(): HasSelection =
      copy(changedFilePaths = emptyList())

    fun someFilesChanged(filePaths: List<String>): ChangedFilesModel {
      return copy(changedFilePaths = filePaths)
    }

    fun unableToFetchChangedFiles(): ChangedFilesModel {
      return copy(isError = true)
    }
  }
}
