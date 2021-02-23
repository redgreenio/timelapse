package redgreen.dawn.affectedfiles.view.model

import redgreen.dawn.affectedfiles.model.AffectedFile
import redgreen.dawn.affectedfiles.view.model.AffectedFileCellViewModel.DirectoryCell
import redgreen.dawn.affectedfiles.view.model.AffectedFileCellViewModel.FileCell

object AffectedFileToCellViewModelMapper {
  fun map(
    affectedFiles: List<AffectedFile>
  ): List<AffectedFileCellViewModel> {
    val groupedAffectedFiles = affectedFiles.groupBy { it.directoryPath }
    val entries = groupedAffectedFiles.entries.toMutableList()
    entries.sortByDescending { it.value.size }

    return entries
      .map { listOf(DirectoryCell(it.key)) + it.value.map(::FileCell).sortedWith(FileCellComparator) }
      .flatten()
  }
}
