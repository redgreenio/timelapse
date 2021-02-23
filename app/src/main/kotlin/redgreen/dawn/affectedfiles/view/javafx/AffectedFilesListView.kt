package redgreen.dawn.affectedfiles.view.javafx

import javafx.beans.InvalidationListener
import javafx.collections.ObservableList
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.ListView
import javafx.scene.control.ScrollBar
import javafx.util.Callback
import redgreen.dawn.affectedfiles.view.model.AffectedFileCellViewModel

/* Courtesy: https://dlsc.com/2017/09/07/javafx-tip-28-pretty-list-view/ */
internal class AffectedFilesListView(
  affectedFiles: ObservableList<AffectedFileCellViewModel>
) : ListView<AffectedFileCellViewModel>(affectedFiles) {
  companion object {
    private const val CSS_CLASS_LIST_VIEW = "dawn-list-view"
    private const val CSS_CLASS_SCROLL_BAR = "dawn-scroll-bar"

    private const val CSS_SELECTOR_VIRTUAL_SCROLL_BAR = "VirtualScrollBar"

    private const val CSS_FILE = "/css/dawn-list-view.css"
  }

  private val verticalScrollbar = ScrollBar().apply {
    isManaged = false
    orientation = Orientation.VERTICAL
    styleClass.add(CSS_CLASS_SCROLL_BAR)
  }

  private val horizontalScrollbar = ScrollBar().apply {
    isManaged = false
    orientation = Orientation.HORIZONTAL
    styleClass.add(CSS_CLASS_SCROLL_BAR)
  }

  init {
    skinProperty().addListener(InvalidationListener {
      bindScrollBars()
      children.addAll(verticalScrollbar, horizontalScrollbar)
    })
    styleClass.add(CSS_CLASS_LIST_VIEW)

    verticalScrollbar.visibleProperty().bind(verticalScrollbar.visibleAmountProperty().isNotEqualTo(0))
    horizontalScrollbar.visibleProperty().bind(horizontalScrollbar.visibleAmountProperty().isNotEqualTo(0))

    stylesheets.add(CSS_FILE)
    cellFactory = Callback { AffectedFilesListCell() }
    isEditable = false
  }

  override fun layoutChildren() {
    super.layoutChildren()
    val prefWidth = verticalScrollbar.prefWidth(-1.0)
    verticalScrollbar.resizeRelocate(
      width - prefWidth - insets.right,
      insets.top,
      prefWidth,
      height - insets.top - insets.bottom
    )

    val prefHeight = horizontalScrollbar.prefHeight(-1.0)
    horizontalScrollbar.resizeRelocate(
      insets.left,
      height - prefHeight - insets.bottom,
      width - insets.left - insets.right,
      prefHeight
    )
  }

  private fun bindScrollBars() {
    val nodes: Set<Node> = lookupAll(CSS_SELECTOR_VIRTUAL_SCROLL_BAR)
    for (node in nodes) {
      if (node is ScrollBar) {
        if (node.orientation == Orientation.VERTICAL) {
          bindScrollBars(verticalScrollbar, node)
        } else if (node.orientation == Orientation.HORIZONTAL) {
          bindScrollBars(horizontalScrollbar, node)
        }
      }
    }
  }

  private fun bindScrollBars(scrollBarA: ScrollBar, scrollBarB: ScrollBar) {
    with(scrollBarA) {
      valueProperty().bindBidirectional(scrollBarB.valueProperty())
      minProperty().bindBidirectional(scrollBarB.minProperty())
      maxProperty().bindBidirectional(scrollBarB.maxProperty())
      visibleAmountProperty().bindBidirectional(scrollBarB.visibleAmountProperty())
      unitIncrementProperty().bindBidirectional(scrollBarB.unitIncrementProperty())
      blockIncrementProperty().bindBidirectional(scrollBarB.blockIncrementProperty())
    }
  }
}
