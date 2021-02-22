package redgreen.dawn.affectedfiles.view.model

import redgreen.dawn.affectedfiles.model.AffectedFile

sealed class AffectedFilesCellViewModel {
  data class FileCell(
    val affectedFile: AffectedFile
  ) : AffectedFilesCellViewModel()

  data class DirectoryCell(
    val path: String
  ) : AffectedFilesCellViewModel()
}
