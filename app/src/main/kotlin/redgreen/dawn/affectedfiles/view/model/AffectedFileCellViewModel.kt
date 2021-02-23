package redgreen.dawn.affectedfiles.view.model

import redgreen.dawn.affectedfiles.model.AffectedFile

sealed class AffectedFileCellViewModel {
  data class FileCell(
    val affectedFile: AffectedFile
  ) : AffectedFileCellViewModel()

  data class DirectoryCell(
    val path: String
  ) : AffectedFileCellViewModel()
}
