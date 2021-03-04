package toys.affectedfiles

import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.redgreen.liftoff.javafx.components.CommitsAffectingFileListView
import io.redgreen.liftoff.javafx.components.DiscoverGitReposComboBox
import io.redgreen.liftoff.javafx.components.TrackedFilesInRepoComboBox
import io.redgreen.timelapse.affectedfiles.contract.AffectedFileContext
import io.redgreen.timelapse.core.GitDirectory
import io.redgreen.timelapse.git.CommitHash
import java.io.File
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import kotlin.properties.Delegates
import org.eclipse.jgit.lib.RepositoryBuilder

class AffectedFilesPropsUi(
  private val affectedFilesContextSubject: BehaviorSubject<AffectedFileContext>,
) : VBox() {
  private companion object {
    private const val GIT_PROJECTS_ROOT = "/Users/ragunathjawahar/GitHubProjects/"
    private const val SPACING = 8.0
  }

  private val gitReposComboBox = DiscoverGitReposComboBox(File(GIT_PROJECTS_ROOT)) { selectedGitRepo = it }

  private val trackedFilesComboBox = TrackedFilesInRepoComboBox { selectedFilePath = it }

  private val commitsAffectingFileListView = CommitsAffectingFileListView {
    val affectedFileContext = getAffectedFilesContext(it)
    affectedFilesContextSubject.onNext(affectedFileContext)
  }

  private val callbackLabel = Label().apply { isWrapText = true }

  private var selectedFilePath: String? by Delegates.observable(null) { _, _, value ->
    value ?: return@observable

    val repository = RepositoryBuilder().setGitDir(selectedGitRepo!!.gitDirectory).build()
    commitsAffectingFileListView.fileModel = CommitsAffectingFileListView.FileModel(repository, selectedFilePath!!)
    callbackLabel.text = null
  }

  private var selectedGitRepo: DiscoverGitReposComboBox.GitRepo? by Delegates.observable(null) { _, _, value ->
    trackedFilesComboBox.gitRepo = value
    commitsAffectingFileListView.fileModel = null
    selectedFilePath = null
  }

  init {
    spacing = SPACING

    children.addAll(
      gitReposComboBox,
      trackedFilesComboBox,
      commitsAffectingFileListView,
      callbackLabel,
    )
  }

  fun showAffectedFile(filePath: String) {
    callbackLabel.text = "Selected file: $filePath"
  }

  private fun getAffectedFilesContext(
    descendentCommitId: String
  ): AffectedFileContext {
    val ancestorCommitHash = getImmediateParent(descendentCommitId)
    val descendentCommitHash = CommitHash(descendentCommitId)

    return AffectedFileContext(
      getGitDirectory(),
      selectedFilePath!!,
      descendentCommitHash,
      ancestorCommitHash
    )
  }

  private fun getImmediateParent(
    descendentCommitId: String
  ): CommitHash {
    val repository = RepositoryBuilder().setGitDir(selectedGitRepo!!.gitDirectory).build()
    val parent = repository.resolve("$descendentCommitId^1")
    return CommitHash(parent.name)
  }

  private fun getGitDirectory(): GitDirectory {
    val repositoryPath = selectedGitRepo!!.gitDirectory.absolutePath
    return GitDirectory.from(repositoryPath).get()
  }
}
