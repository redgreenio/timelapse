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

class OpenRepoEffectHandler {
  companion object {
    private val logger = LoggerFactory.getLogger(ContentViewerEffectHandler::class.java)

    fun from(
      gitDetector: GitDetector,
      recentGitRepositoriesStorage: RecentGitRepositoriesStorage,
      view: OpenRepoView,
      schedulersProvider: SchedulersProvider
    ): ObservableTransformer<OpenRepoEffect, OpenRepoEvent> {
      return RxMobius
        .subtypeEffectHandler<OpenRepoEffect, OpenRepoEvent>()
        .addTransformer(FindGitUsername::class.java, findGitUsernameTransformer(gitDetector, schedulersProvider.io()))
        .addAction(DisplayFileChooser::class.java, { view.displayFileChooser() }, schedulersProvider.ui())
        .addTransformer(
          DetectGitRepository::class.java,
          detectGitRepositoryTransformer(schedulersProvider.io())
        )
        .addConsumer(OpenGitRepository::class.java, { openWorkbench(view, it.path) }, schedulersProvider.ui())
        .addConsumer(
          ShowNotAGitRepositoryError::class.java,
          { view.showNotAGitRepositoryError(it.path) },
          schedulersProvider.ui()
        )
        .addTransformer(
          GetRecentRepositories::class.java,
          getRecentRepositoriesTransformer(recentGitRepositoriesStorage, schedulersProvider.io())
        )
        .build()
    }

    private fun openWorkbench(
      view: OpenRepoView,
      gitRepositoryPath: String
    ) {
      with(view) {
        closeWelcomeStage()
        openGitRepository(gitRepositoryPath)
      }
    }

    private fun findGitUsernameTransformer(
      gitDetector: GitDetector,
      scheduler: Scheduler
    ): ObservableTransformer<FindGitUsername, OpenRepoEvent> {
      return ObservableTransformer { findGitUserNameEvents ->
        findGitUserNameEvents
          .subscribeOn(scheduler)
          .map {
            try {
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
      val gitDirectoryOptional = getGitDirectory(event.path)
      return if (gitDirectoryOptional.isPresent) {
        GitRepositoryDetected(gitDirectoryOptional.get().path)
      } else {
        GitRepositoryNotDetected(event.path)
      }
    }

    private fun getGitDirectory(path: String): Optional<GitDirectory> {
      val maybeGitDirectory = GitDirectory.from(path.appendDotGit())
      return if (maybeGitDirectory.isPresent) {
        maybeGitDirectory
      } else {
        GitDirectory.from(path)
      }
    }

    private fun getRecentRepositoriesTransformer(
      recentGitRepositoriesStorage: RecentGitRepositoriesStorage,
      scheduler: Scheduler
    ): ObservableTransformer<GetRecentRepositories, OpenRepoEvent> {
      return ObservableTransformer { getRecentRepositoriesEvents ->
        getRecentRepositoriesEvents
          .subscribeOn(scheduler)
          .map { toRecentRepositoriesResultEvent(recentGitRepositoriesStorage) }
      }
    }

    private fun toRecentRepositoriesResultEvent(
      recentGitRepositoriesStorage: RecentGitRepositoriesStorage
    ): OpenRepoEvent {
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
}
