package io.redgreen.timelapse.readingarea

import humanize.Humanize
import io.redgreen.timelapse.domain.Change
import io.redgreen.timelapse.domain.getCommit
import io.redgreen.timelapse.visuals.getAuthoredAndCommittedText
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import org.eclipse.jgit.lib.Repository
import kotlin.math.ceil

class CommitInformationPane(private val gitRepository: Repository) : BorderPane() {
  companion object {
    private const val COMMIT_INFORMATION_SEPARATOR = " • "

    private const val PADDING = 10.0
    private const val NO_PADDING = 0.0
  }

  private val messageLabel = Label()
  private val dateTimeInformationLabel = Label()
  private val countProgressInformationLabel = Label().apply {
    padding = Insets(PADDING, NO_PADDING, NO_PADDING, NO_PADDING)
  }
  private val idAuthorInformationLabel = Label()

  init {
    padding = Insets(PADDING)
    center = VBox(messageLabel, dateTimeInformationLabel, countProgressInformationLabel, idAuthorInformationLabel)
  }

  fun showCommitInformation(
    selectedChange: Change,
    currentPosition: Int,
    totalNumberOfCommits: Int
  ) {
    val commit = gitRepository.getCommit(selectedChange.commitId)
    val position = "${currentPosition}/${totalNumberOfCommits}"
    val progressPercent = ((currentPosition).toDouble() / totalNumberOfCommits) * 100
    val progressPercentText = String.format("%.2f", ceil(progressPercent)).replace(".00", "")
    val committedDate = commit.committerIdent.`when`
    val authorAndCommitDate = getAuthoredAndCommittedText(commit.authorIdent.`when`, committedDate)
    val committedNaturalTime = Humanize.naturalTime(committedDate)

    messageLabel.text = selectedChange.message
    dateTimeInformationLabel.text = "$authorAndCommitDate ($committedNaturalTime)"
    countProgressInformationLabel.text = "Commit $position $COMMIT_INFORMATION_SEPARATOR $progressPercentText%"
    idAuthorInformationLabel.text = "${commit.name} $COMMIT_INFORMATION_SEPARATOR ${commit.authorIdent.name}  <${commit.authorIdent.emailAddress}>"
  }
}
