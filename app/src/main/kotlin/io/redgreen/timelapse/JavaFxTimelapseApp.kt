package io.redgreen.timelapse

import io.redgreen.timelapse.openrepo.view.OpenRepoScene
import io.sentry.Sentry
import javafx.application.Application
import javafx.stage.Stage

class JavaFxTimelapseApp : Application() {
  override fun init() {
    super.init()
    // See https://github.com/JabRef/jabref/issues/3295
    System.setProperty("prism.lcdtext", "false")

    Sentry.init { options ->
      options.dsn = "https://9ccbeb20047c42e49a9fbe6094cd9896@o483785.ingest.sentry.io/5536123"
    }
  }

  override fun start(primaryStage: Stage) {
    with(primaryStage) {
      scene = OpenRepoScene()
      isResizable = false
      centerOnScreen()
      show()
    }
  }
}
