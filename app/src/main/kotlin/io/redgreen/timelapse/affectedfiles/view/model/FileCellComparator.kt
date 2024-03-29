package io.redgreen.timelapse.affectedfiles.view.model

import io.redgreen.timelapse.affectedfiles.view.model.AffectedFileCellViewModel.FileCell
import io.redgreen.timelapse.git.model.AffectedFile
import io.redgreen.timelapse.git.model.AffectedFile.Added
import io.redgreen.timelapse.git.model.AffectedFile.Deleted
import io.redgreen.timelapse.git.model.AffectedFile.Modified
import io.redgreen.timelapse.git.model.AffectedFile.Moved

/**
 * Comparators are usually implemented to sort objects in ascending order. This one, on the other hand uses the
 * following criteria to sort affected file cells.
 *
 * 1. Sort file cells based on the following change type order - New, Modified, Moved, and Deleted.
 * 2. Sort file cells of similar change type based on change count in descending order (largest change count first).
 * 3. If file cells of the same type have similar change count, sort them in descending order based on deletions.
 * 4. If there is a tie when using criterion 3, then sort them in ascending order based on filenames.
 * 5. If file cells of the same type don't have deletions, sort them in descending order based on insertions.
 */
object FileCellComparator : Comparator<FileCell> {
  private const val ZERO_BECAUSE_ADDED_FILE_CANT_HAVE_DELETIONS = 0
  private const val BOTH_ARE_SAME = 0

  override fun compare(
    fileCell1: FileCell,
    fileCell2: FileCell
  ): Int {
    val affectedFile1 = fileCell1.affectedFile
    val affectedFile2 = fileCell2.affectedFile

    val rankComparisonResult = getRank(affectedFile1) - getRank(affectedFile2)
    val differentRanks = rankComparisonResult != BOTH_ARE_SAME
    if (differentRanks) {
      return rankComparisonResult
    }

    val changeCountComparisonResult = affectedFile2.changeCount - affectedFile1.changeCount
    val differentChangeCount = changeCountComparisonResult != BOTH_ARE_SAME
    if (differentChangeCount) {
      return changeCountComparisonResult
    }

    val deletionsComparisonResult = getDeletions(affectedFile2) - getDeletions(affectedFile1)
    val differentDeletionsCount = deletionsComparisonResult != BOTH_ARE_SAME
    return if (differentDeletionsCount) {
      deletionsComparisonResult
    } else {
      compareFileNames(affectedFile1.filePath.filename, affectedFile2.filePath.filename)
    }
  }

  private fun getRank(affectedFile: AffectedFile): Int = when (affectedFile) {
    is Added -> 1
    is Modified -> 2
    is Moved -> 3
    is Deleted -> 4
  }

  private fun compareFileNames(
    filename1: String,
    filename2: String
  ): Int =
    filename1.compareTo(filename2)

  private fun getDeletions(
    affectedFile: AffectedFile
  ): Int = when (affectedFile) {
    is Added -> ZERO_BECAUSE_ADDED_FILE_CANT_HAVE_DELETIONS
    is Modified -> affectedFile.deletions
    is Moved -> affectedFile.deletions
    is Deleted -> affectedFile.deletions
  }
}
