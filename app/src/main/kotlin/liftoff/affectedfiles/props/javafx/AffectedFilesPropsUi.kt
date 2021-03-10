package liftoff.affectedfiles.props.javafx

import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.redgreen.architecture.mobius.MobiusDelegate
import io.redgreen.liftoff.javafx.components.DiscoverGitReposComboBox
import io.redgreen.liftoff.javafx.components.DiscoverGitReposComboBox.GitRepo
import io.redgreen.liftoff.javafx.components.FileCommitsListView
import io.redgreen.liftoff.javafx.components.FilesInRepoComboBox
import io.redgreen.timelapse.affectedfiles.contract.AffectedFileContext
import io.redgreen.timelapse.affectedfiles.model.AffectedFile
import io.redgreen.timelapse.core.CommitHash
import io.redgreen.timelapse.core.TrackedFilePath
import io.redgreen.timelapse.foo.fastLazy
import io.redgreen.timelapse.platform.JavaFxSchedulersProvider
import javafx.collections.FXCollections
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import liftoff.affectedfiles.model.AffectingCommit
import liftoff.affectedfiles.props.callback.AffectedFileContextChangeListener
import liftoff.affectedfiles.props.mobius.AffectedFilesPropsUiEffectHandler
import liftoff.affectedfiles.props.mobius.AffectedFilesPropsUiEvent.*
import liftoff.affectedfiles.props.mobius.AffectedFilesPropsUiInit
import liftoff.affectedfiles.props.mobius.AffectedFilesPropsUiModel
import liftoff.affectedfiles.props.mobius.AffectedFilesPropsUiUpdate
import liftoff.affectedfiles.props.view.AffectedFilesPropsUiView
import liftoff.affectedfiles.props.view.AffectedFilesPropsUiViewRenderer

class AffectedFilesPropsUi(
  private val affectedFilesContextSubject: BehaviorSubject<AffectedFileContext>,
) : VBox(), AffectedFilesPropsUiView, AffectedFileContextChangeListener {

  private companion object {
    private const val SPACING = 8.0
  }

  private val delegate by fastLazy {
    MobiusDelegate(
      AffectedFilesPropsUiModel.fetchingGitRepos,
      AffectedFilesPropsUiInit,
      AffectedFilesPropsUiUpdate,
      AffectedFilesPropsUiEffectHandler.from(this@AffectedFilesPropsUi, JavaFxSchedulersProvider),
      AffectedFilesPropsUiViewRenderer(this)
    )
  }

  private val gitReposComboBox = DiscoverGitReposComboBox { gitRepo ->
    delegate.notify(GitRepoSelected(gitRepo))
  }

  private val trackedFilesComboBox = FilesInRepoComboBox { filePath ->
    delegate.notify(FilePathSelected(TrackedFilePath(filePath)))
  }

  private val affectingCommitsListView = FileCommitsListView { commitId, shortMessage ->
    val affectingCommit = AffectingCommit(CommitHash(commitId), shortMessage)
    delegate.notify(AffectingCommitSelected(affectingCommit))
  }

  private val callbackLabel = Label().apply { isWrapText = true }

  init {
    spacing = SPACING

    gitReposComboBox.isDisable = true
    trackedFilesComboBox.isDisable = true
    affectingCommitsListView.isDisable = true

    children.addAll(
      gitReposComboBox,
      trackedFilesComboBox,
      affectingCommitsListView,
      callbackLabel,
    )

    delegate.start() // FIXME: 06/03/21 stop the delegate
  }

  override fun onChange(context: AffectedFileContext) {
    affectedFilesContextSubject.onNext(context)
  }

  override fun populateGitRepos(
    gitRepos: List<GitRepo>
  ) {
    with(gitReposComboBox) {
      isDisable = false
      items = FXCollections.observableArrayList(gitRepos)
    }
  }

  override fun populateTrackedFiles(
    trackedFilePaths: List<TrackedFilePath>
  ) {
    with(trackedFilesComboBox) {
      isDisable = false
      items = FXCollections.observableArrayList(trackedFilePaths.map(TrackedFilePath::value))
    }
  }

  override fun clearTrackedFiles() {
    trackedFilesComboBox.items.clear()
  }

  override fun populateAffectingCommits(
    affectingCommits: List<AffectingCommit>
  ) {
    with(affectingCommitsListView) {
      isDisable = false
      items = FXCollections.observableArrayList(affectingCommits.map { it.commitHash.value to it.shortMessage })
    }
  }

  override fun clearAffectingCommits() {
    affectingCommitsListView.items.clear()
  }

  override fun clearAffectedFileCallbackText() {
    callbackLabel.text = null
  }

  fun showAffectedFile(affectedFile: AffectedFile) {
    callbackLabel.text = affectedFile.toString()
  }
}
