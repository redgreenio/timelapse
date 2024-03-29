package io.redgreen.timelapse.openrepo.view

import io.redgreen.architecture.mobius.MobiusDelegate
import io.redgreen.design.javafx.StackPaneLayers
import io.redgreen.javafx.Fonts
import io.redgreen.timelapse.DISPLAY_VERSION_NAME
import io.redgreen.timelapse.foo.closeWindow
import io.redgreen.timelapse.foo.debug
import io.redgreen.timelapse.foo.exitOnClose
import io.redgreen.timelapse.foo.fastLazy
import io.redgreen.timelapse.foo.launchScene
import io.redgreen.timelapse.main.TimelapseScene
import io.redgreen.timelapse.openrepo.ChooseGitRepository
import io.redgreen.timelapse.openrepo.GitDetector
import io.redgreen.timelapse.openrepo.LargeButton
import io.redgreen.timelapse.openrepo.OpenRecentRepository
import io.redgreen.timelapse.openrepo.OpenRepoEffectHandlerFactory
import io.redgreen.timelapse.openrepo.OpenRepoInit
import io.redgreen.timelapse.openrepo.OpenRepoModel
import io.redgreen.timelapse.openrepo.OpenRepoUpdate
import io.redgreen.timelapse.openrepo.TitledSeparator
import io.redgreen.timelapse.openrepo.WorkingDirectoryChosen
import io.redgreen.timelapse.openrepo.data.RecentGitRepository
import io.redgreen.timelapse.openrepo.storage.PreferencesRecentGitRepositoriesStorage
import io.redgreen.timelapse.openrepo.view.OpenRepoScene.RecentProjectsLayer.NO_RECENT_PROJECTS
import io.redgreen.timelapse.openrepo.view.OpenRepoScene.RecentProjectsLayer.RECENT_PROJECTS_LIST
import io.redgreen.timelapse.openrepo.view.RecentRepositoriesStatus.LOADING
import io.redgreen.timelapse.openrepo.view.RecentRepositoriesStatus.NO_RECENT_REPOSITORIES
import io.redgreen.timelapse.openrepo.view.WelcomeMessage.Greeter
import io.redgreen.timelapse.openrepo.view.WelcomeMessage.Stranger
import io.redgreen.timelapse.platform.JavaFxSchedulersProvider
import io.redgreen.timelapse.router.Destination.WELCOME_SCREEN
import javafx.geometry.Insets
import javafx.geometry.Pos.BOTTOM_RIGHT
import javafx.geometry.Pos.TOP_CENTER
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType.ERROR
import javafx.scene.control.Label
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import java.io.File

private const val SCENE_WIDTH = 720.0
private const val SCENE_HEIGHT = 552.0
private const val NO_PADDING = 0.0

internal const val OPEN_REPOSITORY_TITLE = "Open repository..."
private const val OPEN_REPOSITORY_SUBTITLE = "Choose an existing repository from your local hard drive"

private const val MESSAGE_GET_STARTED = "Let's get started!"

private const val TITLE_RECENT_REPOSITORIES = "Recent repositories"

private val grayTextFill = Color.web("#8F8F8F")

class OpenRepoScene : Scene(StackPane(), SCENE_WIDTH, SCENE_HEIGHT), OpenRepoView {
  companion object {
    fun launch(stage: Stage) {
      launchScene(stage, OpenRepoScene(), false)
      stage.exitOnClose {
        PreferencesRecentGitRepositoriesStorage().setSessionExitDestination(WELCOME_SCREEN)
      }
    }
  }

  private val welcomeUserLabel = Label().apply {
    font = Fonts.robotoRegular(24)
  }

  private val letsGetStartedLabel = Label(MESSAGE_GET_STARTED).apply {
    font = Fonts.robotoLight(18)
    padding = Insets(12.0, NO_PADDING, 28.0, NO_PADDING)
  }

  private val openRepositoryButton = LargeButton(OPEN_REPOSITORY_TITLE, OPEN_REPOSITORY_SUBTITLE).apply {
    setOnMouseClicked { mobiusDelegate.notify(ChooseGitRepository) }
  }

  private val appVersionLabel = Label(DISPLAY_VERSION_NAME).apply {
    font = Fonts.robotoRegular(11)
    padding = Insets(0.0, 24.0, 24.0, 0.0)
    textFill = grayTextFill
  }

  private val recentProjectsList = VBox().apply {
    spacing = 12.0
    padding = Insets(16.0, NO_PADDING, NO_PADDING, NO_PADDING)
  }

  private val recentRepositoriesStatusLabel = Label().apply {
    font = Fonts.robotoRegular(12)
    textFill = grayTextFill
  }

  private val recentProjectsStackPane = StackPane().apply {
    prefHeight = 230.0
    prefWidth = 560.0
  }

  private val recentProjectsLayers = StackPaneLayers<RecentProjectsLayer>(recentProjectsStackPane) {
    recentRepositoriesStatusLabel at NO_RECENT_PROJECTS
    recentProjectsList at RECENT_PROJECTS_LIST
  }

  private val column = VBox().apply {
    children.addAll(
      welcomeUserLabel,
      letsGetStartedLabel,
      Group(openRepositoryButton),
      TitledSeparator(TITLE_RECENT_REPOSITORIES).apply {
        padding = Insets(16.0, NO_PADDING, NO_PADDING, NO_PADDING)
      },
      Group(recentProjectsStackPane)
    )

    padding = Insets(48.0, 16.0, NO_PADDING, 16.0)
    alignment = TOP_CENTER
  }

  private val effectHandlerFactory by fastLazy {
    val gitDetector = GitDetector()
    val recentGitRepositoriesStorage = PreferencesRecentGitRepositoriesStorage()
    OpenRepoEffectHandlerFactory(gitDetector, recentGitRepositoriesStorage, this, JavaFxSchedulersProvider)
  }

  private val mobiusDelegate by fastLazy {
    MobiusDelegate(
      OpenRepoModel.start(),
      OpenRepoInit,
      OpenRepoUpdate,
      effectHandlerFactory.instance(),
      OpenRepoViewRenderer(this)
    )
  }

  init {
    with(root as StackPane) {
      background = Background(BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY))
      children.addAll(column, appVersionLabel)

      StackPane.setAlignment(appVersionLabel, BOTTOM_RIGHT)
    }

    recentProjectsLayers.show(NO_RECENT_PROJECTS)

    mobiusDelegate.start()
  }

  enum class RecentProjectsLayer {
    NO_RECENT_PROJECTS, RECENT_PROJECTS_LIST
  }

  override fun displayWelcomeMessage(
    message: WelcomeMessage
  ) {
    welcomeUserLabel.text = when (message) {
      Stranger -> "Hello, welcome to Timelapse."
      is Greeter -> "Welcome to Timelapse, ${message.displayName}."
    }
  }

  override fun displayRecentRepositoriesStatus(
    status: RecentRepositoriesStatus
  ) {
    recentRepositoriesStatusLabel.text = when (status) {
      LOADING -> "Getting recent repositories..."
      NO_RECENT_REPOSITORIES -> "No recent repositories"
    }
  }

  override fun displayRecentRepositories(
    recentGitRepositories: List<RecentGitRepository>
  ) {
    // FIXME: 12/12/20 This requires a proper fix. The list is duplicated whenever render is called.
    // Call this function only if previous list is different from the current list.
    if (recentProjectsList.children.size != 0) {
      return
    }
    recentProjectsLayers.show(RECENT_PROJECTS_LIST)
    val recentRepositoriesButtons = recentGitRepositories.mapIndexed { index, recentRepository ->
      buildRecentRepositoryButton(recentRepository, index)
    }
    recentProjectsList.children.addAll(recentRepositoriesButtons)
  }

  override fun displayFileChooser() {
    val selectedDirectory = DirectoryChooser()
      .apply { initialDirectory = File(System.getProperty("user.home")) }
      .showDialog(window)
    selectedDirectory?.let {
      mobiusDelegate.notify(WorkingDirectoryChosen(it.absolutePath))
    }
  }

  override fun openGitRepository(path: String) {
    debug { "Opening repository: $path" }
    TimelapseScene.launch(Stage(), path)
  }

  override fun showNotAGitRepositoryError(path: String) {
    Alert(ERROR).apply {
      title = OPEN_REPOSITORY_TITLE
      headerText = "Not a Git repository"
      contentText = "'$path' is not a Git repository."
    }.showAndWait()
  }

  override fun closeWelcomeStage() {
    closeWindow()
  }

  private fun buildRecentRepositoryButton(
    recentGitRepository: RecentGitRepository,
    index: Int
  ): LargeButton {
    val title = recentGitRepository.title()
    val subtitle = recentGitRepository.subtitle()

    return LargeButton(title, subtitle).apply {
      setOnMouseClicked { mobiusDelegate.notify(OpenRecentRepository(index)) }
    }
  }
}
