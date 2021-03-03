package io.redgreen.timelapse.affectedfiles.view.model

import io.redgreen.timelapse.affectedfiles.model.AffectedFile
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Deleted
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Modified
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Moved
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.New
import io.redgreen.timelapse.affectedfiles.view.model.AffectedFileCellViewModel.FileCell
import kotlin.reflect.KClass

fun List<AffectedFileCellViewModel>.summarize(): String {
  val affectedFilesCount = this.size
  val groupedFileCells = this
    .filterIsInstance<FileCell>()
    .groupBy { it.affectedFile::class }

  val homogeneousAffectedFileTypes = groupedFileCells.keys.size == 1
  return if (homogeneousAffectedFileTypes) {
    "$affectedFilesCount ${asText((groupedFileCells.entries.first().value.first().affectedFile))}"
  } else {
    val stats = mutableListOf<String>()

    val new = groupedFileCells.countOf(New::class)
    if (new != 0) stats.add("$new new")

    val modified = groupedFileCells.countOf(Modified::class)
    if (modified != 0) stats.add("$modified modified")

    val moved = groupedFileCells.countOf(Moved::class)
    if (moved != 0) stats.add("$moved moved")

    val deleted = groupedFileCells.countOf(Deleted::class)
    if (deleted != 0) stats.add("$deleted deleted")

    "$affectedFilesCount files affected • ${stats.joinToString()}"
  }
}

private fun Map<KClass<out AffectedFile>, List<FileCell>>.countOf(
  key: KClass<out AffectedFile>
): Int =
  this[key]?.size ?: 0

private fun asText(
  affectedFile: AffectedFile
): String = when (affectedFile) {
  is New -> "new"
  is Modified -> "modified"
  is Moved -> "moved"
  is Deleted -> "deleted"
}
