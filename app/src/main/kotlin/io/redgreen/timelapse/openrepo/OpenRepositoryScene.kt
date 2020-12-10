package io.redgreen.timelapse.openrepo

import io.redgreen.javafx.Fonts
import io.redgreen.timelapse.openrepo.OpenRepositoryScene.RecentProjectsLayer.NO_RECENT_PROJECTS
import io.redgreen.timelapse.openrepo.OpenRepositoryScene.RecentProjectsLayer.RECENT_PROJECTS_LIST
import io.redgreen.timelapse.visuals.StackPaneLayers
import javafx.geometry.Insets
import javafx.geometry.Pos.BOTTOM_RIGHT
import javafx.geometry.Pos.TOP_CENTER
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color

private const val SCENE_WIDTH = 720.0
private const val SCENE_HEIGHT = 552.0
private const val NO_PADDING = 0.0

private const val OPEN_REPOSITORY_TITLE = "Open repository..."
private const val OPEN_REPOSITORY_SUBTITLE = "Choose an existing repository from your local hard drive"

private const val MESSAGE_GET_STARTED = "Let's get started!"
private const val MESSAGE_NO_RECENT_REPOSITORIES = "No recent repositories"

private const val TITLE_RECENT_REPOSITORIES = "Recent repositories"

private const val VERSION_NAME = "v 0.1.8 (pre-alpha)"

private val grayTextFill = Color.web("#8F8F8F")

class OpenRepositoryScene : Scene(StackPane(), SCENE_WIDTH, SCENE_HEIGHT) {
  private val welcomeUserLabel = Label("Welcome to Timelapse, Ajay.").apply {
    font = Fonts.robotoRegular(24)
  }

  private val letsGetStartedLabel = Label(MESSAGE_GET_STARTED).apply {
    font = Fonts.robotoLight(18)
    padding = Insets(12.0, NO_PADDING, 28.0, NO_PADDING)
  }

  private val openRepositoryButton = LargeButton(OPEN_REPOSITORY_TITLE, OPEN_REPOSITORY_SUBTITLE)

  private val appVersionLabel = Label(VERSION_NAME).apply {
    font = Fonts.robotoRegular(11)
    padding = Insets(0.0, 24.0, 24.0, 0.0)
    textFill = grayTextFill
  }

  private val recentProjectsList = VBox().apply {
    spacing = 12.0
    padding = Insets(16.0, NO_PADDING, NO_PADDING, NO_PADDING)

    children.addAll(
      LargeButton(
        "simple-android",
        "~/AndroidStudioProjects/simple-android"
      ),

      LargeButton(
        "retrofit",
        "~/Documents/IdeaProjects/retrofit"
      ),

      LargeButton(
        "angular",
        "~/JsProjects/google/angular"
      )
    )
  }

  private val noRecentRepositoriesLabel = Label(MESSAGE_NO_RECENT_REPOSITORIES).apply {
    font = Fonts.robotoRegular(12)
    textFill = grayTextFill
  }

  private val recentProjectsStackPane = StackPane().apply {
    prefHeight = 230.0
    prefWidth = 560.0

    children.addAll(noRecentRepositoriesLabel, recentProjectsList)
  }

  private val recentProjectsLayers = StackPaneLayers<RecentProjectsLayer>(recentProjectsStackPane)

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

  init {
    with(root as StackPane) {
      background = Background(BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY))
      children.addAll(column, appVersionLabel)

      StackPane.setAlignment(appVersionLabel, BOTTOM_RIGHT)
    }

    with(recentProjectsLayers) {
      setLayer(NO_RECENT_PROJECTS, noRecentRepositoriesLabel)
      setLayer(RECENT_PROJECTS_LIST, recentProjectsList)

      show(NO_RECENT_PROJECTS)
    }
  }

  enum class RecentProjectsLayer {
    NO_RECENT_PROJECTS, RECENT_PROJECTS_LIST
  }
}
