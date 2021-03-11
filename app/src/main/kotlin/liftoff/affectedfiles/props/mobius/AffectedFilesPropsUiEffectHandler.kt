package liftoff.affectedfiles.props.mobius

import com.spotify.mobius.rx3.RxMobius
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.redgreen.liftoff.javafx.components.DiscoverGitReposComboBox
import io.redgreen.liftoff.javafx.components.DiscoverGitReposComboBox.GitRepo
import io.redgreen.timelapse.affectedfiles.contract.AffectedFileContext
import io.redgreen.timelapse.git.model.CommitHash
import io.redgreen.timelapse.git.model.GitDirectory
import io.redgreen.timelapse.git.model.TrackedFilePath
import io.redgreen.timelapse.metrics.GetCommitsMetric
import io.redgreen.timelapse.metrics.GetTrackedFilesMetric
import io.redgreen.timelapse.metrics.publishMetric
import io.redgreen.timelapse.platform.SchedulersProvider
import java.io.File
import liftoff.affectedfiles.model.AffectingCommit
import liftoff.affectedfiles.props.callback.AffectedFileContextChangeListener
import liftoff.affectedfiles.props.mobius.AffectedFilesPropsUiEffect.DiscoverGitRepos
import liftoff.affectedfiles.props.mobius.AffectedFilesPropsUiEffect.GetCommitsAffectingFilePath
import liftoff.affectedfiles.props.mobius.AffectedFilesPropsUiEffect.GetTrackedFiles
import liftoff.affectedfiles.props.mobius.AffectedFilesPropsUiEffect.NotifyCommitSelected
import liftoff.affectedfiles.props.mobius.AffectedFilesPropsUiEvent.CommitsAffectingFilePathFetched
import liftoff.affectedfiles.props.mobius.AffectedFilesPropsUiEvent.GitReposFound
import liftoff.affectedfiles.props.mobius.AffectedFilesPropsUiEvent.TrackedFilePathsFetched
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.lib.RepositoryBuilder
import org.eclipse.jgit.treewalk.TreeWalk

object AffectedFilesPropsUiEffectHandler {
  fun from(
    listener: AffectedFileContextChangeListener,
    schedulersProvider: SchedulersProvider
  ): ObservableTransformer<AffectedFilesPropsUiEffect, AffectedFilesPropsUiEvent> {
    return RxMobius
      .subtypeEffectHandler<AffectedFilesPropsUiEffect, AffectedFilesPropsUiEvent>()
      .addTransformer(DiscoverGitRepos::class.java, discoverGitReposTransformer(schedulersProvider.io()))
      .addTransformer(GetTrackedFiles::class.java, getTrackedFilesTransformer(schedulersProvider.io()))
      .addTransformer(
        GetCommitsAffectingFilePath::class.java,
        getCommitsAffectingFilePathTransformer(schedulersProvider.io())
      )
      .addConsumer(NotifyCommitSelected::class.java, { notifyContextChange(it, listener) }, schedulersProvider.io())
      .build()
  }

  private fun discoverGitReposTransformer(
    ioScheduler: Scheduler
  ): ObservableTransformer<DiscoverGitRepos, AffectedFilesPropsUiEvent> {
    return ObservableTransformer { discoverGitReposEvents ->
      discoverGitReposEvents
        .map { File(it.directoryPath) }
        .flatMapSingle {
          Single
            .fromCallable { naiveGetPossibleGitProjects(it) }
            .subscribeOn(ioScheduler)
            .map { GitReposFound(it) }
        }
    }
  }

  private fun naiveGetPossibleGitProjects(
    gitProjectsRoot: File
  ): List<GitRepo> {
    val gitRepos = gitProjectsRoot
      .list()!!
      .map { File("${gitProjectsRoot.absolutePath}/$it/.git") }
      .filter { it.exists() }
      .map(DiscoverGitReposComboBox::GitRepo)
      .toMutableList()
    gitRepos.sortBy { it.gitDirectory.absolutePath.toLowerCase() }
    return gitRepos
  }

  private fun getTrackedFilesTransformer(
    ioScheduler: Scheduler
  ): ObservableTransformer<GetTrackedFiles, AffectedFilesPropsUiEvent> {
    return ObservableTransformer { getTrackedFilesEvents ->
      getTrackedFilesEvents
        .distinctUntilChanged()
        .flatMapSingle { getTrackedFiles ->
          Single
            .fromCallable {
              getTrackedFilePaths(getTrackedFiles.gitDirectory)
            }
            .publishMetric(::GetTrackedFilesMetric)
            .subscribeOn(ioScheduler)
        }
    }
  }

  private fun getTrackedFilePaths(gitDirectoryPath: String): TrackedFilePathsFetched {
    val repository = RepositoryBuilder()
      .setGitDir(File(gitDirectoryPath))
      .build()

    val headCommit = repository.parseCommit(repository.resolve(Constants.HEAD))
    val filePaths = mutableListOf<TrackedFilePath>()
    TreeWalk(repository).use { treeWalk ->
      treeWalk.reset(headCommit.tree.id)
      treeWalk.isRecursive = true
      while (treeWalk.next()) {
        filePaths.add(TrackedFilePath(treeWalk.pathString))
      }
    }

    return TrackedFilePathsFetched(filePaths.toList())
  }

  private fun getCommitsAffectingFilePathTransformer(
    ioScheduler: Scheduler
  ): ObservableTransformer<GetCommitsAffectingFilePath, AffectedFilesPropsUiEvent> {
    return ObservableTransformer { getCommitsAffectingFilePathEvents ->
      getCommitsAffectingFilePathEvents
        .flatMapSingle { getCommitsAffectingFilePath ->
          Single.fromCallable {
            val repository = RepositoryBuilder()
              .setGitDir(File(getCommitsAffectingFilePath.gitDirectory))
              .build()
            val affectingCommits = getAffectingCommits(repository, getCommitsAffectingFilePath)
            CommitsAffectingFilePathFetched(affectingCommits)
          }
            .publishMetric(::GetCommitsMetric)
            .subscribeOn(ioScheduler)
        }
    }
  }

  private fun getAffectingCommits(
    repository: Repository,
    getCommitsAffectingFilePath: GetCommitsAffectingFilePath
  ): List<AffectingCommit> {
    return Git(repository)
      .log()
      .addPath(getCommitsAffectingFilePath.filePath.value)
      .call()
      .map { AffectingCommit(CommitHash(it.name), it.shortMessage) }
  }

  private fun notifyContextChange(
    notifyCommitSelected: NotifyCommitSelected,
    listener: AffectedFileContextChangeListener
  ) {
    val gitDirectoryPath = notifyCommitSelected.gitRepo.gitDirectory.absolutePath
    val repository = RepositoryBuilder().setGitDir(File(gitDirectoryPath)).build()
    val gitDirectory = GitDirectory.from(gitDirectoryPath).get()

    val descendentCommitHash = notifyCommitSelected.affectingCommit.commitHash
    val ancestorCommitHash = getAncestorCommitHash(repository, descendentCommitHash.value)

    val context = AffectedFileContext(
      gitDirectory,
      notifyCommitSelected.filePath,
      descendentCommitHash,
      ancestorCommitHash
    )
    listener.onChange(context)
  }

  private fun getAncestorCommitHash(
    repository: Repository,
    descendentCommitId: String
  ): CommitHash {
    val parentCommitId = repository.resolve("$descendentCommitId^1")?.name
      ?: descendentCommitId
    return CommitHash(parentCommitId)
  }
}
