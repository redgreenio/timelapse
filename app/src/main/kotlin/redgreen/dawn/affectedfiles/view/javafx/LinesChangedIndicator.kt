package redgreen.dawn.affectedfiles.view.javafx

import io.redgreen.timelapse.foo.fastLazy
import javafx.geometry.Insets
import javafx.scene.Group
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.text.Font
import redgreen.dawn.extentions.backgroundFillRoundedCorners

class LinesChangedIndicator : Group() {
  companion object {
    private const val HEX_INSERTIONS = "#629749"
    private const val HEX_DELETIONS = "#F05545"

    private const val HAIR_SPACE = ' '

    private const val FONT_FAMILY = "Roboto Bold"
    private const val FONT_SIZE = 12.0

    private const val PREFIX_PLUS = '+'
    private const val PREFIX_MINUS = '-'

    private const val PADDING_VERTICAL = 1.4
    private const val PADDING_HORIZONTAL = 5.0

    private const val CSS_CLASS_INDICATOR_LABEL = "indicator-label"
  }

  private val linesChangedFont by fastLazy { Font.font(FONT_FAMILY, FONT_SIZE) }

  private val deletionsLabel = newLabel(HEX_DELETIONS)
  private val insertionsLabel = newLabel(HEX_INSERTIONS)

  init {
    children.add(HBox(deletionsLabel, insertionsLabel).apply { spacing = 4.0 })
  }

  fun linesChanged(deletions: Int, insertions: Int) {
    updateCount(deletionsLabel, PREFIX_MINUS, deletions)
    updateCount(insertionsLabel, PREFIX_PLUS, insertions)
  }

  private fun newLabel(backgroundHex: String): Label {
    return Label().apply {
      styleClass.add(CSS_CLASS_INDICATOR_LABEL)
      font = linesChangedFont
      padding = Insets(PADDING_VERTICAL, PADDING_HORIZONTAL, PADDING_VERTICAL, PADDING_HORIZONTAL)

      backgroundFillRoundedCorners(backgroundHex)
    }
  }

  private fun updateCount(label: Label, prefix: Char, count: Int) {
    with(label) {
      text = "$prefix$HAIR_SPACE$count"
      isVisible = count > 0
      isManaged = isVisible
    }
  }
}
