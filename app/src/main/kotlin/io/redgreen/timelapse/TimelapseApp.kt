package io.redgreen.timelapse

import io.redgreen.timelapse.main.TimelapseScene
import io.redgreen.timelapse.openrepo.view.OpenRepoScene
import io.sentry.Sentry
import javafx.application.Application
import javafx.application.Platform
import javafx.stage.Stage
import kotlin.system.exitProcess

private const val ALPHA = "\uD835\uDEFC"
private const val APP_NAME = "Timelapse (Pre-$ALPHA)"
private const val APP_VERSION = "0.1.10" // FIXME: 12/12/20 The version name should be via the build process.

internal const val DISPLAY_VERSION_NAME = "v$APP_VERSION (pre-alpha)"

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
    val repositoryPath = if (parameters.raw.size != 0) parameters.raw.first() else null
    val shouldLaunchTimelapseScene = repositoryPath != null
    val sceneToShow = if (shouldLaunchTimelapseScene) {
      TimelapseScene(repositoryPath!!)
    } else {
      OpenRepoScene()
    }

    with(primaryStage) {
      scene = sceneToShow
      isResizable = shouldLaunchTimelapseScene
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
