package io.redgreen.timelapse.affectedfiles.view.model

import io.redgreen.timelapse.affectedfiles.model.AffectedFile
import io.redgreen.timelapse.affectedfiles.view.model.AffectedFileCellViewModel.DirectoryCell
import io.redgreen.timelapse.affectedfiles.view.model.AffectedFileCellViewModel.FileCell

object AffectedFileToCellViewModelMapper {
  fun map(
    affectedFiles: List<AffectedFile>
  ): List<AffectedFileCellViewModel> {
    val groupedAffectedFiles = affectedFiles.groupBy { it.directoryPath }
    val entries = groupedAffectedFiles.entries.toMutableList()
    entries.sortByDescending { it.value.size }

    return entries
      .map { listOf(DirectoryCell(it.key, it.value.size)) + it.value.map(::FileCell).sortedWith(FileCellComparator) }
      .flatten()
  }
}
