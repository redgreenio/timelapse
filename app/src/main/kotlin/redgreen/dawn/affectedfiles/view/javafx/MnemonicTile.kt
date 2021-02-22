package redgreen.dawn.affectedfiles.view.javafx

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.paint.Color.WHITE
import javafx.scene.text.Font
import javafx.scene.text.FontWeight.BLACK
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment
import redgreen.dawn.affectedfiles.model.AffectedFile
import redgreen.dawn.affectedfiles.model.AffectedFile.Deleted
import redgreen.dawn.affectedfiles.model.AffectedFile.Modified
import redgreen.dawn.affectedfiles.model.AffectedFile.Moved
import redgreen.dawn.affectedfiles.model.AffectedFile.New

class MnemonicTile : Group() {
  private companion object {
    private const val WIDTH = 24.0
    private const val HEIGHT = WIDTH

    private const val LETTER_NEW = 'N'
    private const val LETTER_MODIFIED = 'M'
    private const val LETTER_MOVED = 'V'
    private const val LETTER_DELETED = 'D'

    private const val HEX_NEW = "0x4CAF50"
    private const val HEX_MODIFIED = "0x3B9DFF"
    private const val HEX_MOVED = "0xAB47BC"
    private const val HEX_DELETED = "0xF44336"

    private const val FONT_FAMILY = "Roboto Black"
    private const val FONT_SIZE = 12.0

    private const val CORNER_RADIUS = 1.5
  }

  private val mnemonicLetterText = Text().apply {
    fill = WHITE
    textAlignment = TextAlignment.CENTER
    font = Font.font(FONT_FAMILY, BLACK, FONT_SIZE)
  }

  private val mnemonicContainer = StackPane().apply {
    prefWidth = WIDTH; prefHeight = HEIGHT
    alignment = Pos.CENTER
    children.add(mnemonicLetterText)
  }

  init {
    children.add(mnemonicContainer)
  }

  fun setAffectedFile(affectedFile: AffectedFile) {
    val (letter, hexColor) = when (affectedFile) {
      is New -> LETTER_NEW to HEX_NEW
      is Modified -> LETTER_MODIFIED to HEX_MODIFIED
      is Moved -> LETTER_MOVED to HEX_MOVED
      is Deleted -> LETTER_DELETED to HEX_DELETED
    }

    setMnemonicChar(letter)
    setBackgroundFill(hexColor)
  }

  private fun setMnemonicChar(letter: Char) {
    mnemonicLetterText.text = "$letter"
  }

  private fun setBackgroundFill(hexColor: String) {
    mnemonicContainer.background = Background(BackgroundFill(Color.web(hexColor), CornerRadii(CORNER_RADIUS), Insets.EMPTY))
  }
}
