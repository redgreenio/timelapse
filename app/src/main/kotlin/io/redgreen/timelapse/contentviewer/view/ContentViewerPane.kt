package io.redgreen.timelapse.contentviewer.view

import io.redgreen.timelapse.contentviewer.CommitIdClicked
import io.redgreen.timelapse.contentviewer.ContentViewerEffectHandler
import io.redgreen.timelapse.contentviewer.ContentViewerModel
import io.redgreen.timelapse.contentviewer.ContentViewerUpdate
import io.redgreen.timelapse.contentviewer.FileAndRevisionSelected
import io.redgreen.timelapse.diff.DiffViewer
import io.redgreen.timelapse.diff.FormattedDiff
import io.redgreen.timelapse.domain.BlobDiff
import io.redgreen.timelapse.domain.BlobDiff.Simple
import io.redgreen.timelapse.foo.fastLazy
import io.redgreen.timelapse.mobius.MobiusDelegate
import io.redgreen.timelapse.platform.JavaFxClipboardService
import io.redgreen.timelapse.platform.JavaFxSchedulersProvider
import io.redgreen.timelapse.vcs.git.GitRepositoryService
import javafx.scene.layout.BorderPane
import org.eclipse.jgit.lib.Repository

class ContentViewerPane(
  gitRepository: Repository
) : BorderPane(), ContentViewerView {
  private val repositoryService = GitRepositoryService(gitRepository)

  private val mobiusDelegate by fastLazy {
    MobiusDelegate(
      ContentViewerModel.noFileAndRevisionSelected(),
      ContentViewerUpdate,
      ContentViewerEffectHandler.from(repositoryService, JavaFxClipboardService(), JavaFxSchedulersProvider),
      ContentViewerViewRenderer(this)
    )
  }

  private val summaryBar = SummaryBar()
  private val diffViewer = DiffViewer()

  init {
    top = summaryBar
    center = diffViewer
    summaryBar.onCommitIdClicked = { mobiusDelegate.notify(CommitIdClicked) }

    mobiusDelegate.start()
  }

  fun selectFileAndCommitId(selectedFilePath: String, commitId: String) {
    mobiusDelegate.notify(FileAndRevisionSelected(selectedFilePath, commitId))
  }

  override fun fileNameLabelVisible(visible: Boolean) {
    // TODO: 05-12-20201
  }

  override fun deletionsInsertionsAndFilesChangedLabelsVisible(visible: Boolean) {
    // TODO: 05-12-2020
  }

  override fun commitIdLabelVisible(visible: Boolean) {
    // TODO: 05-12-2020
  }

  override fun commitMessageLabelVisible(visible: Boolean) {
    // TODO: 05-12-2020
  }

  override fun setFileName(filePath: String) {
    summaryBar.setFileName(filePath)
  }

  override fun setCommitId(commitId: String) {
    summaryBar.setCommitId(commitId)
  }

  override fun setDeletionsInsertionsAndFilesChanged(
    deletions: Int,
    insertions: Int,
    changedFiles: Int
  ) {
    with(summaryBar) {
      setDeletions(deletions)
      setInsertions(insertions)
      setFilesChanged(changedFiles)
    }
  }

  override fun setCommitMessage(message: String) {
    summaryBar.setCommitMessage(message)
  }

  override fun displaySelectFileMessage() {
    // TODO: 05-12-2020
  }

  override fun displayLoadingMessage() {
    // TODO: 05-12-2020
  }

  override fun displayContent(blobDiff: BlobDiff) {
    val rawDiff = when (blobDiff) {
      is Simple -> blobDiff.rawDiff
      is BlobDiff.Merge -> blobDiff.diffs.first().rawDiff
    }
    diffViewer.showDiff(FormattedDiff.from((rawDiff)))
  }
}
