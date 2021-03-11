package io.redgreen.timelapse.affectedfiles.view.model

import io.redgreen.timelapse.affectedfiles.view.model.AffectedFileCellViewModel.DirectoryCell
import io.redgreen.timelapse.affectedfiles.view.model.AffectedFileCellViewModel.FileCell
import io.redgreen.timelapse.git.model.AffectedFile

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

  private val AffectedFile.directoryPath: String
    get() {
      val path = filePath.value.substring(0, filePath.value.lastIndexOf('/') + 1)
      return if (path == "") "/" else path
    }
}
