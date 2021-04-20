package io.redgreen.timelapse.affectedfiles.view.javafx

import javafx.scene.Group
import javafx.scene.control.Label
import javafx.scene.layout.HBox

class LinesChangedIndicators : Group() {
  companion object {
    private const val CSS_FILE = "/css/affected-files/lines-changed-indicator.css"

    private const val CSS_CLASS_INDICATORS = "indicators"
    private const val CSS_CLASS_DELETIONS = "deletions"
    private const val CSS_CLASS_INSERTIONS = "insertions"

    private const val PREFIX_PLUS = '+'
    private const val PREFIX_MINUS = '-'
    private const val HAIR_SPACE = ' '

    private const val SPACING_BETWEEN_LABELS = 4.0
  }

  private val deletionsLabel = newLabel(CSS_CLASS_DELETIONS)
  private val insertionsLabel = newLabel(CSS_CLASS_INSERTIONS)

  init {
    stylesheets.add(CSS_FILE)
    children.add(HBox(deletionsLabel, insertionsLabel).apply { spacing = SPACING_BETWEEN_LABELS })
  }

  fun linesChanged(deletions: Int, insertions: Int) {
    updateCount(deletionsLabel, PREFIX_MINUS, deletions)
    updateCount(insertionsLabel, PREFIX_PLUS, insertions)
  }

  private fun newLabel(cssClass: String): Label {
    return Label().apply { styleClass.addAll(CSS_CLASS_INDICATORS, cssClass) }
  }

  private fun updateCount(label: Label, prefix: Char, count: Int) {
    with(label) {
      text = "$prefix$HAIR_SPACE$count"
      isVisible = count > 0
      isManaged = isVisible
    }
  }
}
