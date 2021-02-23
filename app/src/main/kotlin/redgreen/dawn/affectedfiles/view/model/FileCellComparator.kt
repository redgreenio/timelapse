package redgreen.dawn.affectedfiles.view.model

import redgreen.dawn.affectedfiles.model.AffectedFile
import redgreen.dawn.affectedfiles.model.AffectedFile.Deleted
import redgreen.dawn.affectedfiles.model.AffectedFile.Modified
import redgreen.dawn.affectedfiles.model.AffectedFile.Moved
import redgreen.dawn.affectedfiles.model.AffectedFile.New
import redgreen.dawn.affectedfiles.view.model.AffectedFileCellViewModel.FileCell

/**
 * Comparators are usually implemented to sort file in ascending order. This one on the other hand sorts files in
 * a mix. It uses ascending and descending order based on certain criteria.
 *
 * 1. Sort file cells based on the following change type order - New, Modified, Moved, and Deleted
 * 2. Sort file cells of similar change type based on change count in descending order.
 * 3. If file cells of the same type have similar change count, sort them in descending order based on deletions.
 * 4. If file cells of the same type don't have deletions, sort them in descending order based on insertions.
 * 5. If file cells of the same type have similar deletions, sort them in ascending order based on file names.
 */
object FileCellComparator : Comparator<FileCell> {
  private val affectedFileRanks = mapOf(
    New::class to 1,
    Modified::class to 2,
    Moved::class to 3,
    Deleted::class to 4,
  )

  override fun compare(
    fileCell1: FileCell,
    fileCell2: FileCell
  ): Int {
    val affectedFile1 = fileCell1.affectedFile
    val affectedFile2 = fileCell2.affectedFile

    val rankComparisonResult = affectedFileRanks[affectedFile1::class]!! - affectedFileRanks[affectedFile2::class]!!
    return if (rankComparisonResult != 0) {
      rankComparisonResult
    } else {
      val changeCountComparisonResult = affectedFile2.changeCount - affectedFile1.changeCount
      if (changeCountComparisonResult != 0) {
        changeCountComparisonResult
      } else {
        val deletionsComparisonResult = getDeletions(affectedFile2) - getDeletions(affectedFile1)
        if (deletionsComparisonResult != 0) {
          deletionsComparisonResult
        } else {
          compareFileNames(affectedFile1.filename, affectedFile2.filename)
        }
      }
    }
  }

  private fun compareFileNames(
    filename1: String,
    filename2: String
  ): Int {
    return filename1.compareTo(filename2)
  }

  private fun getDeletions(
    affectedFile: AffectedFile
  ): Int = when (affectedFile) {
    is New -> 0
    is Modified -> affectedFile.deletions
    is Moved -> affectedFile.deletions
    is Deleted -> affectedFile.deletions
  }
}
