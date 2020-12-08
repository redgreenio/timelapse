package io.redgreen.visuals.demo

import io.redgreen.javafx.Fonts
import io.redgreen.timelapse.openrepo.LargeButton
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color

private val grayTextFill = Color.web("#8F8F8F")

internal fun openRepoScene(): Scene {
  return Scene(StackPane().apply {
    background = Background(BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY))

    val openRepoButton = LargeButton(
      "Open repository...",
      "Choose an existing repository from your local hard drive"
    )

    val appVersionLabel = Label("v 0.1.8 (pre-alpha)").apply {
      font = Fonts.robotoRegular(11)
      padding = Insets(0.0, 24.0, 24.0, 0.0)
      textFill = grayTextFill
    }

    children.addAll(getWelcomeMessages(), Group(createRecentProjectsList(openRepoButton)), appVersionLabel)

    alignment = Pos.CENTER
    StackPane.setAlignment(appVersionLabel, Pos.BOTTOM_RIGHT)
  }, 720.0, 552.0)
}

private fun getWelcomeMessages(): VBox {
  return VBox().apply {
    val welcomeLabel = Label("Welcome to Timelapse, Ajay.").apply {
      font = Fonts.robotoRegular(24)
    }

    val letsGetStartedLabel = Label("Let's get started!").apply {
      font = Fonts.robotoLight(18)
    }

    spacing = 8.0
    children.addAll(welcomeLabel, letsGetStartedLabel)
    alignment = Pos.TOP_CENTER
    padding = Insets(48.0, 16.0, 16.0, 16.0)
  }
}

private fun createRecentProjectsList(node: Node): VBox {
  val separator = HBox().apply {
    val recentProjectsLabel = Label("Recent repositories").apply {
      font = Fonts.robotoRegular(12)
      textFill = grayTextFill
      padding = Insets(2.0, 4.0, 2.0, 4.0)
    }

    val beforeLine = Pane().apply {
      background = Background(BackgroundFill(grayTextFill, CornerRadii.EMPTY, Insets.EMPTY))
      prefHeight = 1.2
      prefWidth  = 16.0
    }

    val afterLine = Pane().apply {
      background  = Background(BackgroundFill(grayTextFill, CornerRadii.EMPTY, Insets.EMPTY))
      prefHeight = 1.2
      prefWidth  = 430.0
    }

    children.addAll(Group(beforeLine), recentProjectsLabel, Group(afterLine))
    alignment = Pos.CENTER

    prefWidth = 560.0
  }

  return VBox().apply {
    spacing = 16.0

    children.addAll(
      node,

      separator,

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
      ),
    )
  }
}
