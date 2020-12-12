package io.redgreen.timelapse

import io.redgreen.timelapse.openrepo.view.OpenRepoScene
import io.sentry.Sentry
import javafx.application.Application
import javafx.application.Platform
import javafx.stage.Stage
import kotlin.system.exitProcess

private const val ALPHA = "\uD835\uDEFC"
private const val APP_NAME = "Timelapse (Pre-$ALPHA)"

class TimelapseApp : Application() {
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
      title = APP_NAME
      centerOnScreen()
      show()

      setOnCloseRequest {
        // FIXME: 19-11-2020 The application does not exit due to running thread pool executors? Shut down Mobius loops?
        Platform.exit()
        exitProcess(0)
      }
    }
  }
}
