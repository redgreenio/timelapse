package io.redgreen.timelapse.ui

import io.redgreen.timelapse.visuals.DiffLine
import javax.swing.JComponent

interface DiffDisplay {
  val codeComponent: JComponent
  fun showDiff(diffLines: List<DiffLine>)
}
