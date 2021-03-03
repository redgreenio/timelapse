package io.redgreen.timelapse.affectedfiles.view.model

import io.redgreen.timelapse.affectedfiles.model.AffectedFile

sealed class AffectedFileCellViewModel {
  data class FileCell(
    val affectedFile: AffectedFile
  ) : AffectedFileCellViewModel()

  data class DirectoryCell(
    val path: String,
    val fileCount: Int
  ) : AffectedFileCellViewModel()
}
