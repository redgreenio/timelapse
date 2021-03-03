package toys.affectedfiles

import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.redgreen.liftoff.LiftOff
import io.redgreen.liftoff.javafx.components.CommitsAffectingFileListView
import io.redgreen.liftoff.javafx.components.CommitsAffectingFileListView.FileModel
import io.redgreen.liftoff.javafx.components.DiscoverGitReposComboBox
import io.redgreen.liftoff.javafx.components.DiscoverGitReposComboBox.GitRepo
import io.redgreen.liftoff.javafx.components.TrackedFilesInRepoComboBox
import java.io.File
import javafx.application.Application
import javafx.geometry.Dimension2D
import javafx.scene.control.Label
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import kotlin.properties.Delegates
import org.eclipse.jgit.lib.RepositoryBuilder
import redgreen.dawn.affectedfiles.contract.AffectedFileContext
import redgreen.dawn.affectedfiles.contract.AffectedFilesProps
import redgreen.dawn.affectedfiles.view.javafx.AffectedFilesEntryPointPane

fun main() {
  Application.launch(AffectedFilesLiftOff::class.java)
}

class AffectedFilesLiftOff : LiftOff<AffectedFilesProps, AffectedFilesEntryPointPane>() {
  private companion object {
    private const val TITLE = "Affected files"
    private const val WIDTH = 500.0
    private const val HEIGHT = 600.0

    private const val GIT_PROJECTS_ROOT = "/Users/ragunathjawahar/GitHubProjects/"
  }

  private val contextChanges = BehaviorSubject.create<AffectedFileContext>()

  private val callbackLabel = Label().apply { isWrapText = true }

  override fun title(): String = TITLE

  override fun howBig(): Dimension2D =
    Dimension2D(WIDTH, HEIGHT)

  override fun entryPoint(): AffectedFilesEntryPointPane =
    AffectedFilesEntryPointPane()

  override fun props(): AffectedFilesProps {
    return AffectedFilesProps(contextChanges) {
      callbackLabel.text = "Selected file: $it"
    }
  }

  private var selectedFilePath: String? by Delegates.observable(null) { _, _, value ->
    value ?: return@observable

    val repository = RepositoryBuilder().setGitDir(selectedGitRepo!!.gitDirectory).build()
    commitsAffectingFileListView.fileModel = FileModel(repository, selectedFilePath!!)
  }

  private var selectedGitRepo: GitRepo? by Delegates.observable(null) { _, _, value ->
    trackedFilesComboBox.gitRepo = value
    commitsAffectingFileListView.fileModel = null
    selectedFilePath = null
  }

  private val gitReposComboBox = DiscoverGitReposComboBox(File(GIT_PROJECTS_ROOT)) { selectedGitRepo = it }
  private val trackedFilesComboBox = TrackedFilesInRepoComboBox { selectedFilePath = it }
  private val commitsAffectingFileListView = CommitsAffectingFileListView { println(it) }

  override fun propsUi(): Region {
    return VBox().apply {
      spacing = 8.0
      children.addAll(
        gitReposComboBox,
        trackedFilesComboBox,
        commitsAffectingFileListView,
        callbackLabel,
      )
    }
  }
}
