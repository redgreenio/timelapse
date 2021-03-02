package redgreen.design

import io.redgreen.timelapse.foo.fastLazy
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox

open class TitledParent : GridPane() {
  private companion object {
    private const val CSS_FILE = "/css/design/titled-parent.css"

    private const val CSS_CLASS_TITLED_PARENT = "titled-parent"
    private const val CSS_CLASS_TITLE = "title"
  }

  private val column = VBox()

  private val titleLabel by fastLazy {
    Label().apply { styleClass.add(CSS_CLASS_TITLE) }
  }

  init {
    stylesheets.add(CSS_FILE)
    styleClass.add(CSS_CLASS_TITLED_PARENT)

    column.prefWidthProperty().bind(widthProperty())
    titleLabel.prefWidthProperty().bind(column.widthProperty())
  }

  fun setTitle(title: String) {
    titleLabel.text = title
  }

  fun setContent(title: String, content: Node) {
    addRow(0, column)

    with(column.children) {
      addAll(titleLabel.apply { text = title }, content)
    }
  }
}
