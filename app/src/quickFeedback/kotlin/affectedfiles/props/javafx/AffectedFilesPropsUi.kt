package affectedfiles.props.javafx

import affectedfiles.model.AffectingCommit
import affectedfiles.props.callback.AffectedFileContextChangeListener
import affectedfiles.props.mobius.AffectedFilesPropsUiEffectHandler
import affectedfiles.props.mobius.AffectedFilesPropsUiEvent.AffectingCommitSelected
import affectedfiles.props.mobius.AffectedFilesPropsUiEvent.FilePathSelected
import affectedfiles.props.mobius.AffectedFilesPropsUiEvent.GitRepoSelected
import affectedfiles.props.mobius.AffectedFilesPropsUiInit
import affectedfiles.props.mobius.AffectedFilesPropsUiModel
import affectedfiles.props.mobius.AffectedFilesPropsUiUpdate
import affectedfiles.props.view.AffectedFilesPropsUiView
import affectedfiles.props.view.AffectedFilesPropsUiViewRenderer
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.redgreen.architecture.mobius.MobiusDelegate
import io.redgreen.liftoff.javafx.components.DiscoverGitDirectoriesComboBox
import io.redgreen.liftoff.javafx.components.FileCommitsListView
import io.redgreen.liftoff.javafx.components.FilesInRepoComboBox
import io.redgreen.timelapse.affectedfiles.contract.AffectedFileContext
import io.redgreen.timelapse.foo.fastLazy
import io.redgreen.timelapse.git.model.AffectedFile
import io.redgreen.timelapse.git.model.CommitHash
import io.redgreen.timelapse.git.model.GitDirectory
import io.redgreen.timelapse.git.model.TrackedFilePath
import io.redgreen.timelapse.platform.JavaFxSchedulersProvider
import javafx.collections.FXCollections
import javafx.scene.control.Label
import javafx.scene.layout.VBox

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

  private val gitReposComboBox = DiscoverGitDirectoriesComboBox { gitRepo ->
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

  override fun populateGitDirectories(
    gitDirectories: List<GitDirectory>
  ) {
    with(gitReposComboBox) {
      isDisable = false
      items = FXCollections.observableArrayList(gitDirectories)
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
