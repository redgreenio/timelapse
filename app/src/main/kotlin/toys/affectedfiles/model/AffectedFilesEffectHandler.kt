package toys.affectedfiles.model

import com.spotify.mobius.rx3.RxMobius
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.redgreen.liftoff.javafx.components.DiscoverGitReposComboBox
import io.redgreen.liftoff.javafx.components.DiscoverGitReposComboBox.GitRepo
import io.redgreen.timelapse.affectedfiles.contract.AffectedFileContext
import io.redgreen.timelapse.core.CommitHash
import io.redgreen.timelapse.core.GitDirectory
import io.redgreen.timelapse.core.TrackedFilePath
import io.redgreen.timelapse.platform.SchedulersProvider
import java.io.File
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.lib.RepositoryBuilder
import org.eclipse.jgit.treewalk.TreeWalk
import toys.affectedfiles.AffectedFileContextChangeListener
import toys.affectedfiles.model.AffectedFilesEffect.DiscoverGitRepos
import toys.affectedfiles.model.AffectedFilesEffect.GetCommitsAffectingFilePath
import toys.affectedfiles.model.AffectedFilesEffect.GetTrackedFiles
import toys.affectedfiles.model.AffectedFilesEffect.NotifyCommitSelected
import toys.affectedfiles.model.AffectedFilesEvent.CommitsAffectingFilePathFetched
import toys.affectedfiles.model.AffectedFilesEvent.GitReposFound
import toys.affectedfiles.model.AffectedFilesEvent.TrackedFilePathsFetched

object AffectedFilesEffectHandler {
  fun from(
    listener: AffectedFileContextChangeListener,
    schedulersProvider: SchedulersProvider
  ): ObservableTransformer<AffectedFilesEffect, AffectedFilesEvent> {
    return RxMobius
      .subtypeEffectHandler<AffectedFilesEffect, AffectedFilesEvent>()
      .addTransformer(DiscoverGitRepos::class.java, discoverGitReposTransformer(schedulersProvider.io()))
      .addTransformer(GetTrackedFiles::class.java, getTrackedFilesTransformer(schedulersProvider.io()))
      .addTransformer(
        GetCommitsAffectingFilePath::class.java,
        getCommitsAffectingFilePathTransformer(schedulersProvider)
      )
      .addConsumer(NotifyCommitSelected::class.java, { notifyContextChange(it, listener) }, schedulersProvider.io())
      .build()
  }

  private fun discoverGitReposTransformer(
    ioScheduler: Scheduler
  ): ObservableTransformer<DiscoverGitRepos, AffectedFilesEvent> {
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
  ): ObservableTransformer<GetTrackedFiles, AffectedFilesEvent> {
    return ObservableTransformer { getTrackedFilesEvents ->
      getTrackedFilesEvents
        .distinctUntilChanged()
        .flatMapSingle { getTrackedFiles ->
          Single
            .fromCallable {
              getTrackedFilePaths(getTrackedFiles.gitDirectory)
            }
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
    schedulersProvider: SchedulersProvider
  ): ObservableTransformer<GetCommitsAffectingFilePath, AffectedFilesEvent> {
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
            .subscribeOn(schedulersProvider.io())
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
    val resolvedAncestorCommitId = repository
      .resolve("${descendentCommitHash.value}^1")?.name ?: descendentCommitHash.value
    val ancestorCommitHash = CommitHash(resolvedAncestorCommitId)
    listener.onChange(AffectedFileContext(gitDirectory, notifyCommitSelected.filePath, descendentCommitHash, ancestorCommitHash))
  }
}
