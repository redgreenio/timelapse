package io.redgreen.timelapse.fileexplorer.view

sealed class FileExplorerSelection {
  object AllFiles : FileExplorerSelection() {
    override fun toString(): String =
      "All Files"
  }

  data class TMinusDays(val days: Int) : FileExplorerSelection() {
    override fun toString(): String =
      "T-$days days"
  }
}
