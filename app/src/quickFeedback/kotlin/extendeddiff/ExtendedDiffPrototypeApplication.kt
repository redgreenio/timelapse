package extendeddiff

import io.redgreen.timelapse.foo.getResourceUrl
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import java.net.URL

class ExtendedDiffPrototypeApplication : Application() {
  override fun start(primaryStage: Stage) {
    val fxmlLoader = FXMLLoader()
    val parent = fxmlLoader.load<BorderPane>(getUrlForResource().openStream())
    val extendedDiffScene = Scene(parent)

    with(primaryStage) {
      title = "Extended Diff (Prototype)"
      scene = extendedDiffScene
      show()
    }

    val controller = fxmlLoader.getController<ExtendedDiffController>()
    controller.start()
  }

  private fun getUrlForResource(): URL {
    return getResourceUrl("/fxml/extended-diff-prototype.fxml")!!
  }
}

fun main() {
  Application.launch(ExtendedDiffPrototypeApplication::class.java)
}
