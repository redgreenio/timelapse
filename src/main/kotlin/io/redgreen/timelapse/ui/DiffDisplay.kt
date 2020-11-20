package io.redgreen.timelapse.ui

import io.redgreen.timelapse.diff.FormattedDiff
import javafx.scene.Node

interface DiffDisplay {
  val codeNode: Node
  fun showDiff(diff: FormattedDiff)
  fun focusOnDiff()
}
