package io.redgreen.timelapse.openrepo

import com.spotify.mobius.rx3.RxMobius
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.core.Scheduler
import io.redgreen.timelapse.contentviewer.ContentViewerEffectHandler
import io.redgreen.timelapse.foo.appendDotGit
import io.redgreen.timelapse.git.model.GitDirectory
import io.redgreen.timelapse.openrepo.storage.RecentGitRepositoriesStorage
import io.redgreen.timelapse.openrepo.view.OpenRepoView
import io.redgreen.timelapse.platform.SchedulersProvider
import org.slf4j.LoggerFactory
import java.util.Optional

class OpenRepoEffectHandlerFactory(
  private val gitDetector: GitDetector,
  private val recentGitRepositoriesStorage: RecentGitRepositoriesStorage,
  private val view: OpenRepoView,
  private val schedulersProvider: SchedulersProvider,
) {
  private val logger = LoggerFactory.getLogger(ContentViewerEffectHandler::class.java)

  fun instance(): ObservableTransformer<OpenRepoEffect, OpenRepoEvent> {
    val ioScheduler = schedulersProvider.io()
    val uiScheduler = schedulersProvider.ui()

    return RxMobius
      .subtypeEffectHandler<OpenRepoEffect, OpenRepoEvent>()
      .addTransformer(FindGitUsername::class.java, findGitUsernameTransformer(ioScheduler))
      .addAction(DisplayFileChooser::class.java, { view.displayFileChooser() }, uiScheduler)
      .addTransformer(DetectGitRepository::class.java, detectGitRepositoryTransformer(ioScheduler))
      .addConsumer(OpenGitRepository::class.java, { openWorkbench(it.path) }, uiScheduler)
      .addConsumer(ShowNotAGitRepositoryError::class.java, { view.showNotAGitRepositoryError(it.path) }, uiScheduler)
      .addTransformer(GetRecentRepositories::class.java, getRecentRepositoriesTransformer(ioScheduler))
      .build()
  }

  private fun findGitUsernameTransformer(scheduler: Scheduler): ObservableTransformer<FindGitUsername, OpenRepoEvent> {
    return ObservableTransformer { findGitUserNameEvents ->
      findGitUserNameEvents
        .subscribeOn(scheduler)
        .map { toGitUsernameEvent() }
    }
  }

  private fun toGitUsernameEvent(): OpenRepoEvent {
    return try {
      val globalUsernameOptional = gitDetector.globalUsername()
      if (globalUsernameOptional.isPresent) {
        GitUsernameFound(globalUsernameOptional.get())
      } else {
        GitUsernameNotFound
      }
    } catch (e: RuntimeException) {
      logger.error("${FindGitUsername::class.java.name} failed.", e)
      GitUsernameNotFound
    }
  }

  private fun detectGitRepositoryTransformer(
    scheduler: Scheduler
  ): ObservableTransformer<DetectGitRepository, OpenRepoEvent> {
    return ObservableTransformer { events ->
      events
        .map(::toDetectGitRepositoryResult)
        .subscribeOn(scheduler)
    }
  }

  private fun toDetectGitRepositoryResult(event: DetectGitRepository): OpenRepoEvent {
    val gitDirectoryOptional = getGitDirectory(event.workingDirectoryPath)
    return if (gitDirectoryOptional.isPresent) {
      GitRepositoryDetected(gitDirectoryOptional.get().path)
    } else {
      GitRepositoryNotDetected(event.workingDirectoryPath)
    }
  }

  private fun getGitDirectory(workingDirectoryPath: String): Optional<GitDirectory> {
    val maybeGitDirectory = GitDirectory.from(workingDirectoryPath.appendDotGit())
    return if (maybeGitDirectory.isPresent) {
      maybeGitDirectory
    } else {
      GitDirectory.from(workingDirectoryPath)
    }
  }

  private fun openWorkbench(gitRepositoryPath: String) {
    with(view) {
      closeWelcomeStage()
      openGitRepository(gitRepositoryPath)
    }
  }

  private fun getRecentRepositoriesTransformer(
    scheduler: Scheduler
  ): ObservableTransformer<GetRecentRepositories, OpenRepoEvent> {
    return ObservableTransformer { getRecentRepositoriesEvents ->
      getRecentRepositoriesEvents
        .subscribeOn(scheduler)
        .map { toRecentRepositoriesResultEvent() }
    }
  }

  private fun toRecentRepositoriesResultEvent(): OpenRepoEvent {
    return try {
      val recentRepositories = recentGitRepositoriesStorage.getRecentRepositories()
      if (recentRepositories.isNotEmpty()) {
        HasRecentRepositories(recentRepositories)
      } else {
        NoRecentRepositories
      }
    } catch (e: RuntimeException) {
      logger.error("${GetRecentRepositories::class.java.name} failed.", e)
      UnableToGetRecentRepositories
    }
  }
}
