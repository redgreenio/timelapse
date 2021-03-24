package io.redgreen.timelapse.experiments

import io.redgreen.timelapse.foo.fastLazy
import io.redgreen.timelapse.search.FilePathIndex
import io.redgreen.timelapse.search.Match
import io.redgreen.timelapse.search.Occurrence.None
import io.redgreen.timelapse.search.Occurrence.Segment
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.stage.Stage
import javafx.util.Callback

fun main() {
  Application.launch(TimelapseExperimentApp::class.java)
}

class TimelapseExperimentApp : Application() {
  companion object {
    private const val WIDTH = 300.0
    private const val HEIGHT = 400.0
  }

  private val filePaths = listOf(
    "io/redgreen/timelapse/diff/Patch.kt",
    "io/redgreen/timelapse/diff/FormattedDiff.kt",
    "io/redgreen/timelapse/diff/DiffLine.kt",
    "io/redgreen/timelapse/diff/DiffViewer.kt",
    "io/redgreen/timelapse/diff/PatchViewer.kt",
  )
  private val filePathIndex = FilePathIndex.from(filePaths)

  private val searchTextField by fastLazy {
    TextField().apply {
      textProperty().addListener { _, _, term ->
        showMatches(filePathIndex.search(term))
      }
    }
  }

  private val matchesListView by fastLazy {
    ListView<Match>().apply {
      cellFactory = Callback<ListView<Match>, ListCell<Match>> { MatchCell() }
    }
  }

  override fun start(primaryStage: Stage) {
    val borderPane = BorderPane().apply {
      top = searchTextField
      center = matchesListView
    }

    showMatches(filePaths.map(::Match))

    with(primaryStage) {
      scene = Scene(borderPane, WIDTH, HEIGHT)
      show()
    }
  }

  override fun init() {
    super.init()
    System.setProperty("prism.lcdtext", "false")
  }

  private fun showMatches(matches: List<Match>) {
    with(matchesListView.items) {
      clear()
      addAll(matches)
    }
  }

  class MatchCell : ListCell<Match>() {
    override fun updateItem(match: Match?, empty: Boolean) {
      super.updateItem(match, empty)
      if (empty) {
        text = null
        graphic = null
      }

      if (match != null) {
        val matchText = match.text
        val occurrences = match.occurrences

        val isEmptySearchTerm = occurrences.size == 1 && occurrences[0] is None
        graphic = if (isEmptySearchTerm) {
          TextFlow(Text(matchText))
        } else {
          val textFlow = occurrences.foldRight(TextFlow()) { occurrence, textFlow ->
            textFlow.apply {
              val texts = if (occurrence is Segment) {
                val formattedMatch = mutableListOf<Text>()
                if (occurrence.startIndex != 0 && textFlow.children.isEmpty()) {
                  formattedMatch.add(Text(matchText.substring(0, occurrence.startIndex)))
                }
                formattedMatch.add(
                  Text(matchText.substring(occurrence.startIndex, occurrence.startIndex + occurrence.chars))
                    .apply { fill = Color.CRIMSON }
                )
                formattedMatch
              } else {
                listOf(Text(matchText))
              }
              children.addAll(texts)
            }
          }

          val lastOccurrence = occurrences.last()
          if (lastOccurrence is Segment && lastOccurrence.startIndex + lastOccurrence.chars != matchText.length) {
            val highlightedText = textFlow.children.map { it as Text }.joinToString("") { it.text }
            textFlow.children.add(Text(matchText.substring(highlightedText.length)))
          }

          textFlow
        }
      }
    }
  }
}
