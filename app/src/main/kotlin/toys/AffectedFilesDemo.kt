package toys

import javafx.application.Application
import javafx.collections.FXCollections
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage
import redgreen.dawn.affectedfiles.affectedFiles
import redgreen.dawn.affectedfiles.view.javafx.AffectedFilesListView

class AffectedFilesDemo : Application() {
  override fun start(primaryStage: Stage) {
    with(primaryStage) {
      title = "Affected Files"
      val root = Pane()
      val rootScene = Scene(root, 600.0, 600.0)
      loadCssFiles(rootScene)

      root.apply {
        val affectedFiles = FXCollections.observableList(affectedFiles.toMutableList())
        val affectedFilesListView = AffectedFilesListView(affectedFiles)
        affectedFilesListView.prefWidthProperty().bind(this.widthProperty())
        children.add(affectedFilesListView)
      }

      scene = rootScene
      show()
      centerOnScreen()
    }
  }

  override fun init() {
    System.setProperty("prism.lcdtext", "false")
  }

  private fun loadCssFiles(scene: Scene) {
    val cssResourcesPath = "/css/"
    val cssFiles = listOf("fonts.css")

    cssFiles
      .map { cssFile -> "$cssResourcesPath$cssFile" }
      .map { AffectedFilesDemo::class.java.getResource("/css/fonts.css").toExternalForm() }
      .onEach { cssUri -> scene.stylesheets.add(cssUri) }
  }
}

fun main() {
  Application.launch(AffectedFilesDemo::class.java)
}
