package io.redgreen.timelapse.affectedfiles.view.javafx

import io.redgreen.timelapse.git.model.AffectedFile
import io.redgreen.timelapse.git.model.AffectedFile.Added
import io.redgreen.timelapse.git.model.AffectedFile.Deleted
import io.redgreen.timelapse.git.model.AffectedFile.Modified
import io.redgreen.timelapse.git.model.AffectedFile.Moved
import javafx.scene.control.Label

class MnemonicTile : Label() {
  private companion object {
    private const val CSS_FILE = "/css/affected-files/mnemonic-tile.css"

    private const val CSS_CLASS_MNEMONIC_TILE = "mnemonic-tile"
    private const val CSS_CLASS_NEW = "new"
    private const val CSS_CLASS_MODIFIED = "modified"
    private const val CSS_CLASS_MOVED = "moved"
    private const val CSS_CLASS_DELETED = "deleted"

    private const val LETTER_ADDED = 'A'
    private const val LETTER_MODIFIED = 'M'
    private const val LETTER_MOVED = 'V'
    private const val LETTER_DELETED = 'D'
  }

  enum class MnemonicAppearance(
    val letter: Char,
    val cssClass: String
  ) {
    Added(LETTER_ADDED, CSS_CLASS_NEW),
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
      is Added -> MnemonicAppearance.Added
      is Modified -> MnemonicAppearance.Modified
      is Moved -> MnemonicAppearance.Moved
      is Deleted -> MnemonicAppearance.Deleted
    }

    text = "${appearance.letter}"

    val classesToRemove = MnemonicAppearance
      .values()
      /* Minus the current CSS class. Otherwise, the label will flicker until we add the css class back to
       * the change type. */
      .map(MnemonicAppearance::cssClass) - appearance.cssClass

    with(styleClass) {
      removeAll(classesToRemove)
      add(appearance.cssClass)
    }
  }
}
