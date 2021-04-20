package io.redgreen.timelapse.affectedfiles.view.model

import io.redgreen.timelapse.affectedfiles.view.model.AffectedFileCellViewModel.FileCell
import io.redgreen.timelapse.git.model.AffectedFile
import io.redgreen.timelapse.git.model.AffectedFile.Added
import io.redgreen.timelapse.git.model.AffectedFile.Deleted
import io.redgreen.timelapse.git.model.AffectedFile.Modified
import io.redgreen.timelapse.git.model.AffectedFile.Moved
import kotlin.reflect.KClass

private const val ADDED = "added"
private const val MODIFIED = "modified"
private const val MOVED = "moved"
private const val DELETED = "deleted"

private const val TEMPLATE_ADDED = "%d $ADDED"
private const val TEMPLATE_MODIFIED = "%d $MODIFIED"
private const val TEMPLATE_MOVED = "%d $MOVED"
private const val TEMPLATE_DELETED = "%d $DELETED"

private const val TEMPLATE_VARIOUS_STATS = "%d files affected • %s"

fun List<AffectedFileCellViewModel>.summarize(): String {
  val affectedFilesCount = this.size
  val groupedFileCells = this
    .filterIsInstance<FileCell>()
    .groupBy { it.affectedFile::class }

  val homogeneousAffectedFileTypes = groupedFileCells.keys.size == 1
  return if (homogeneousAffectedFileTypes) {
    "$affectedFilesCount ${humanizedStat((groupedFileCells.entries.first().value.first().affectedFile))}"
  } else {
    val stats = mutableListOf<String>()

    val added = groupedFileCells.countOf(Added::class)
    if (added != 0) stats.add(String.format(TEMPLATE_ADDED, added))

    val modified = groupedFileCells.countOf(Modified::class)
    if (modified != 0) stats.add(String.format(TEMPLATE_MODIFIED, modified))

    val moved = groupedFileCells.countOf(Moved::class)
    if (moved != 0) stats.add(String.format(TEMPLATE_MOVED, moved))

    val deleted = groupedFileCells.countOf(Deleted::class)
    if (deleted != 0) stats.add(String.format(TEMPLATE_DELETED, deleted))

    String.format(TEMPLATE_VARIOUS_STATS, affectedFilesCount, stats.joinToString())
  }
}

private fun Map<KClass<out AffectedFile>, List<FileCell>>.countOf(
  key: KClass<out AffectedFile>
): Int =
  this[key]?.size ?: 0

private fun humanizedStat(
  affectedFile: AffectedFile
): String = when (affectedFile) {
  is Added -> ADDED
  is Modified -> MODIFIED
  is Moved -> MOVED
  is Deleted -> DELETED
}
