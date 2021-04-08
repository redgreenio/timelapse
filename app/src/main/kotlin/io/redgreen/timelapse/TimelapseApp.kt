package io.redgreen.timelapse

import io.redgreen.timelapse.main.TimelapseScene
import io.redgreen.timelapse.openrepo.storage.PreferencesRecentGitRepositoriesStorage
import io.redgreen.timelapse.openrepo.view.OpenRepoScene
import io.redgreen.timelapse.router.SessionLauncher
import io.sentry.Sentry
import javafx.application.Application
import javafx.stage.Stage

private const val ALPHA = "\uD835\uDEFC"
internal const val APP_NAME = "Timelapse Studio (Pre-$ALPHA)"
private const val APP_VERSION = "1.0.0" // FIXME: 12/12/20 The version name should be via the build process.

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
    SessionLauncher(PreferencesRecentGitRepositoriesStorage()).tryRestorePreviousSession(
      { TimelapseScene.launch(primaryStage, it) },
      { OpenRepoScene.launch(primaryStage) }
    )
  }
}
