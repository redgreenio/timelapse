package toys.affectedfiles

import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.redgreen.liftoff.javafx.components.CommitsAffectingFileListView
import io.redgreen.liftoff.javafx.components.DiscoverGitReposComboBox
import io.redgreen.liftoff.javafx.components.TrackedFilesInRepoComboBox
import java.io.File
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import kotlin.properties.Delegates
import org.eclipse.jgit.lib.RepositoryBuilder
import redgreen.dawn.affectedfiles.contract.AffectedFileContext

class AffectedFilesPropsUi(
  private val affectedFilesContextSubject: BehaviorSubject<AffectedFileContext>,
) : VBox() {
  private companion object {
    private const val GIT_PROJECTS_ROOT = "/Users/ragunathjawahar/GitHubProjects/"
    private const val SPACING = 8.0
  }

  private val gitReposComboBox = DiscoverGitReposComboBox(File(GIT_PROJECTS_ROOT)) { selectedGitRepo = it }
  private val trackedFilesComboBox = TrackedFilesInRepoComboBox { selectedFilePath = it }
  private val commitsAffectingFileListView = CommitsAffectingFileListView { println(it) }
  private val callbackLabel = Label().apply { isWrapText = true }

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
    callbackLabel.text = "Affected file: $filePath"
  }

  private var selectedFilePath: String? by Delegates.observable(null) { _, _, value ->
    value ?: return@observable

    val repository = RepositoryBuilder().setGitDir(selectedGitRepo!!.gitDirectory).build()
    commitsAffectingFileListView.fileModel = CommitsAffectingFileListView.FileModel(repository, selectedFilePath!!)
  }

  private var selectedGitRepo: DiscoverGitReposComboBox.GitRepo? by Delegates.observable(null) { _, _, value ->
    trackedFilesComboBox.gitRepo = value
    commitsAffectingFileListView.fileModel = null
    selectedFilePath = null
  }
}
