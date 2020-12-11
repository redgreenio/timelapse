package io.redgreen.timelapse.openrepo

import com.spotify.mobius.rx3.RxMobius
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.core.Scheduler
import io.redgreen.timelapse.contentviewer.ContentViewerEffectHandler
import io.redgreen.timelapse.openrepo.data.RecentRepository
import io.redgreen.timelapse.openrepo.view.OpenRepoView
import io.redgreen.timelapse.platform.SchedulersProvider
import org.slf4j.LoggerFactory

class OpenRepoEffectHandler {
  companion object {
    private val logger = LoggerFactory.getLogger(ContentViewerEffectHandler::class.java)

    fun from(
      gitDetector: GitDetector,
      recentRepositoriesRepository: RecentRepositoriesRepository,
      view: OpenRepoView,
      schedulersProvider: SchedulersProvider
    ): ObservableTransformer<OpenRepoEffect, OpenRepoEvent> {
      return RxMobius
        .subtypeEffectHandler<OpenRepoEffect, OpenRepoEvent>()
        .addTransformer(FindGitUsername::class.java, findGitUsernameTransformer(gitDetector, schedulersProvider.io()))
        .addAction(DisplayFileChooser::class.java, { view.displayFileChooser() }, schedulersProvider.ui())
        .addTransformer(
          DetectGitRepository::class.java,
          detectGitRepositoryTransformer(gitDetector, schedulersProvider.io())
        )
        .addConsumer(
          UpdateRecentRepositories::class.java,
          { recentRepositoriesRepository.update(RecentRepository(it.path)) },
          schedulersProvider.io()
        )
        .addConsumer(OpenGitRepository::class.java, { view.openGitRepository(it.path) }, schedulersProvider.ui())
        .addConsumer(
          ShowNotAGitRepositoryError::class.java,
          { view.showNotAGitRepositoryError(it.path) },
          schedulersProvider.ui()
        )
        .build()
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
      gitDetector: GitDetector,
      scheduler: Scheduler
    ): ObservableTransformer<DetectGitRepository, OpenRepoEvent> {
      return ObservableTransformer { events ->
        events
          .map {
            if (gitDetector.isGitRepository(it.path)) {
              GitRepositoryDetected(it.path)
            } else {
              GitRepositoryNotDetected(it.path)
            }
          }
          .subscribeOn(scheduler)
      }
    }
  }
}
