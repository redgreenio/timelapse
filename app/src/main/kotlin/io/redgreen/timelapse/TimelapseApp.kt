package io.redgreen.timelapse

import io.redgreen.timelapse.main.TimelapseScene
import io.redgreen.timelapse.openrepo.storage.PreferencesRecentGitRepositoriesStorage
import io.redgreen.timelapse.openrepo.view.OpenRepoScene
import io.redgreen.timelapse.router.SessionLauncher
import io.sentry.Sentry
import javafx.application.Application
import javafx.stage.Stage

internal const val APP_NAME = "Timelapse Studio (Friends-only Edition)"
private const val APP_VERSION = "2021.0.2"

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
