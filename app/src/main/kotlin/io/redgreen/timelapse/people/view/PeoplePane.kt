package io.redgreen.timelapse.people.view

import io.redgreen.timelapse.foo.fastLazy
import io.redgreen.timelapse.vcs.Contribution
import io.redgreen.timelapse.vcs.git.GitRepositoryService
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY
import javafx.scene.layout.BorderPane
import org.eclipse.jgit.lib.Repository

class PeoplePane(private val gitRepository: Repository) : BorderPane() {
  companion object {
    private const val COLUMN_NAME = "Name"
    private const val COLUMN_CONTRIBUTION = "Contribution %"
  }

  private val gitRepositoryService by fastLazy { GitRepositoryService(gitRepository) }

  private val titleLabel by fastLazy { Label().apply { style = "-fx-font-weight: bold" } }

  private val contributorsTable by fastLazy {
    TableView<Contribution>().apply {
      isEditable = false
      columnResizePolicy = CONSTRAINED_RESIZE_POLICY

      val nameColumn = TableColumn<Contribution, String>(COLUMN_NAME).apply {
        setCellValueFactory { SimpleObjectProperty(it.value.author.name) }
      }

      val contributionColumn = TableColumn<Contribution, String>(COLUMN_CONTRIBUTION).apply {
        style = "-fx-alignment: CENTER-RIGHT;"
        setCellValueFactory { SimpleObjectProperty(String.format("%.02f", it.value.fraction * 100)) }
      }

      columns.addAll(nameColumn, contributionColumn)
    }
  }

  init {
    top = titleLabel
    center = contributorsTable
    setTitle()
  }

  fun selectFileAndRevision(filePath: String, commitId: String) {
    gitRepositoryService
      .getContributions(commitId, filePath)
      .subscribe { contributions ->
        setTitle(contributions.size)
        with(contributorsTable) {
          items = FXCollections.observableArrayList(*contributions.toTypedArray())
        }
      }
  }

  private fun setTitle(peopleCount: Int = 0) {
    val title = if (peopleCount == 0) "People" else "People ($peopleCount)"
    Platform.runLater { titleLabel.text = " • $title • " }
  }
}
