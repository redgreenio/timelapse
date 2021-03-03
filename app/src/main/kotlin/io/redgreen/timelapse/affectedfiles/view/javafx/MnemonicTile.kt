package io.redgreen.timelapse.affectedfiles.view.javafx

import io.redgreen.timelapse.affectedfiles.model.AffectedFile
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Deleted
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Modified
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.Moved
import io.redgreen.timelapse.affectedfiles.model.AffectedFile.New
import javafx.scene.control.Label

class MnemonicTile : Label() {
  private companion object {
    private const val CSS_FILE = "/css/affected-files/mnemonic-tile.css"

    private const val CSS_CLASS_MNEMONIC_TILE = "mnemonic-tile"
    private const val CSS_CLASS_NEW = "new"
    private const val CSS_CLASS_MODIFIED = "modified"
    private const val CSS_CLASS_MOVED = "moved"
    private const val CSS_CLASS_DELETED = "deleted"

    private const val LETTER_NEW = 'N'
    private const val LETTER_MODIFIED = 'M'
    private const val LETTER_MOVED = 'V'
    private const val LETTER_DELETED = 'D'
  }

  enum class MnemonicAppearance(
    val letter: Char,
    val cssClass: String
  ) {
    New(LETTER_NEW, CSS_CLASS_NEW),
    Modified(LETTER_MODIFIED, CSS_CLASS_MODIFIED),
    Moved(LETTER_MOVED, CSS_CLASS_MOVED),
    Deleted(LETTER_DELETED, CSS_CLASS_DELETED),
  }

  init {
    styleClass.add(CSS_CLASS_MNEMONIC_TILE)
    stylesheets.add(CSS_FILE)
  }

  fun setAffectedFile(affectedFile: AffectedFile) {
    val appearance = when (affectedFile) {
      is New -> MnemonicAppearance.New
      is Modified -> MnemonicAppearance.Modified
      is Moved -> MnemonicAppearance.Moved
      is Deleted -> MnemonicAppearance.Deleted
    }

    text = "${appearance.letter}"

    val classesToRemove = MnemonicAppearance
      .values()
      /* Minus the current CSS class. Otherwise, the label will flicker until we add the css class for the change type */
      .map(MnemonicAppearance::cssClass) - appearance.cssClass

    with(styleClass) {
      removeAll(classesToRemove)
      add(appearance.cssClass)
    }
  }
}
