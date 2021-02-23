package toys

import javafx.application.Application
import javafx.collections.FXCollections
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage
import redgreen.dawn.affectedfiles.affectedFilesViewModels
import redgreen.dawn.affectedfiles.view.javafx.AffectedFilesListView
import redgreen.dawn.affectedfiles.view.model.AffectedFileCellViewModel

class AffectedFilesDemo : Application() {
  private val affectedFileViewModelsMutableList: MutableList<AffectedFileCellViewModel> = affectedFilesViewModels
    .toMutableList()

  override fun start(primaryStage: Stage) {
    with(primaryStage) {
      title = "Affected Files"
      val root = Pane()
      val rootScene = Scene(root, 500.0, 600.0)
      loadCssFiles(rootScene)

      val affectedFilesViewModels = FXCollections
        .observableList(affectedFileViewModelsMutableList)
      val affectedFilesListView = AffectedFilesListView(affectedFilesViewModels)
      affectedFilesListView.prefWidthProperty().bind(root.widthProperty())
      affectedFilesListView.prefHeightProperty().bind(root.heightProperty())

      root.children.add(affectedFilesListView)

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
