package extendeddiff

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import java.net.URL

class ExtendedDiffPrototypeApplication : Application() {
  override fun start(primaryStage: Stage) {
    val parent = FXMLLoader.load<BorderPane>(getUrlForResource())
    val extendedDiffScene = Scene(parent)

    with(primaryStage) {
      title = "Extended Diff (Prototype)"
      scene = extendedDiffScene
      show()
    }
  }

  private fun getUrlForResource(): URL {
    return ExtendedDiffPrototypeApplication::class.java.getResource("/fxml/extended-diff-prototype.fxml")
  }
}

fun main() {
  Application.launch(ExtendedDiffPrototypeApplication::class.java)
}
