package toys.affectedfiles

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.redgreen.liftoff.javafx.components.DiscoverGitReposComboBox
import io.redgreen.liftoff.javafx.components.DiscoverGitReposComboBox.GitRepo
import io.redgreen.liftoff.javafx.components.FileCommitsListView
import io.redgreen.liftoff.javafx.components.FilesInRepoComboBox
import io.redgreen.timelapse.affectedfiles.contract.AffectedFileContext
import io.redgreen.timelapse.core.GitDirectory
import io.redgreen.timelapse.git.CommitHash
import java.io.File
import java.util.Optional
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.lib.RepositoryBuilder

class AffectedFilesPropsUi(
  private val affectedFilesContextSubject: BehaviorSubject<AffectedFileContext>,
) : VBox() {
  private companion object {
    private const val GIT_PROJECTS_ROOT = "/Users/ragunathjawahar/GitHubProjects/"
    private const val SPACING = 8.0
  }

  private val gitRepoSelections = PublishSubject.create<GitRepo>()
  private val fileSelections = PublishSubject.create<Optional<String>>()
  private val commitSelections = PublishSubject.create<Optional<String>>()

  private val gitReposComboBox = DiscoverGitReposComboBox(File(GIT_PROJECTS_ROOT)) {
    gitRepoSelections.onNext(it)
    fileSelections.onNext(Optional.empty())
    commitSelections.onNext(Optional.empty())
  }

  private val filesInRepoComboBox = FilesInRepoComboBox { filePath ->
    fileSelections.onNext(Optional.of(filePath))
    commitSelections.onNext(Optional.empty())
  }

  private val fileCommitsListView = FileCommitsListView { commitId ->
    commitSelections.onNext(Optional.of(commitId))
  }

  private val callbackLabel = Label().apply { isWrapText = true }

  init {
    spacing = SPACING

    children.addAll(
      gitReposComboBox,
      filesInRepoComboBox,
      fileCommitsListView,
      callbackLabel,
    )

    Observable
      .combineLatest<GitRepo, Optional<String>, Optional<String>, Triple<GitRepo, Optional<String>, Optional<String>>>(
        gitRepoSelections,
        fileSelections,
        commitSelections,
        ::Triple
      )
      .doOnNext { (gitRepo, fileOptional, commitOptional) ->
        resetUiAfterSelection(gitRepo, fileOptional, commitOptional)
      }
      .filter { (_, fileOptional, commitOptional) ->
        fileOptional.isPresent && commitOptional.isPresent
      }
      .map { (gitRepo, fileOptional, commitOptional) ->
        mapToAffectedFileContext(gitRepo, fileOptional, commitOptional)
      }
      .subscribe(affectedFilesContextSubject::onNext)
  }

  private fun resetUiAfterSelection(
    gitRepo: GitRepo,
    fileOptional: Optional<String>,
    commitOptional: Optional<String>
  ) {
    if (filesInRepoComboBox.gitRepo != gitRepo) {
      filesInRepoComboBox.gitRepo = gitRepo
      fileCommitsListView.fileModel = null
    } else {
      fileCommitsListView.fileModel = if (fileOptional.isPresent) {
        FileCommitsListView.FileModel(getRepository(gitRepo.gitDirectory), fileOptional.get())
      } else {
        null
      }
    }
    callbackLabel.text = null

    if (fileOptional.isEmpty || commitOptional.isEmpty) {
      // TODO Send a different message to clear selection
    }
  }

  private fun mapToAffectedFileContext(
    gitRepo: GitRepo,
    fileOptional: Optional<String>,
    commitOptional: Optional<String>
  ): AffectedFileContext {
    val gitDirectory = GitDirectory.from(gitRepo.gitDirectory.absolutePath).get()
    val filePath = fileOptional.get()
    val descendent = CommitHash(commitOptional.get())
    val ancestor = getImmediateParent(gitDirectory, descendent.value)

    return AffectedFileContext(gitDirectory, filePath, descendent, ancestor)
  }

  private fun getRepository(gitDirectory: File): Repository =
    RepositoryBuilder().setGitDir(gitDirectory).build()

  fun showAffectedFile(filePath: String) {
    callbackLabel.text = "Selected file: $filePath"
  }

  private fun getImmediateParent(
    gitDirectory: GitDirectory,
    descendentCommitId: String
  ): CommitHash {
    val repository = RepositoryBuilder().setGitDir(File(gitDirectory.path)).build()
    val parent = repository.resolve("$descendentCommitId^1")
    return CommitHash(parent.name)
  }
}
