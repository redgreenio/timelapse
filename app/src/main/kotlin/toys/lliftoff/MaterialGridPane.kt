package toys.lliftoff

import io.redgreen.javafx.ResizableCanvas
import io.redgreen.timelapse.foo.fastLazy
import io.redgreen.timelapse.visuals.debug.debug
import io.redgreen.timelapse.visuals.debug.drawDebugGrid
import javafx.scene.canvas.GraphicsContext
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import redgreen.dawn.extentions.matchParent

// TODO: 24/02/21 Rename to `DesignerGridPane`
class MaterialGridPane : Pane() {
  init {
    debug = true
  }

  private val materialGridCanvas by fastLazy { MaterialGridCanvas() }

  fun setOnlyChild(node: Region) {
    isMouseTransparent = true

    with(children) {
      clear()
      add(node)
      add(materialGridCanvas.apply { isMouseTransparent = true })
    }

    materialGridCanvas.matchParent(this)
    node.matchParent(this)
  }
}

private class MaterialGridCanvas : ResizableCanvas() {
  private companion object {
    private const val GRID_COLOR = "#C2185BFA"
    private const val GRID_SIZE = 8
  }

  override fun GraphicsContext.draw() {
    drawDebugGrid(width, height, GRID_SIZE, Color.web(GRID_COLOR))
  }
}
