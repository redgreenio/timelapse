package io.redgreen.timelapse.changedfiles

import com.spotify.mobius.rx3.RxMobius
import io.reactivex.rxjava3.core.ObservableTransformer
import io.redgreen.timelapse.changedfiles.contracts.ReadingAreaContract
import io.redgreen.timelapse.vcs.ChangedFile
import io.redgreen.timelapse.vcs.VcsRepositoryService
import org.slf4j.LoggerFactory

class ChangedFilesEffectHandler private constructor() {
  companion object {
    private val logger = LoggerFactory.getLogger(ChangedFilesEffectHandler::class.java)

    fun from(
      vcsRepositoryService: VcsRepositoryService,
      readingAreaContract: ReadingAreaContract
    ): ObservableTransformer<ChangedFilesEffect, ChangedFilesEvent> {
      return RxMobius
        .subtypeEffectHandler<ChangedFilesEffect, ChangedFilesEvent>()
        .addTransformer(GetChangedFiles::class.java, getChangedFilesTransformer(vcsRepositoryService))
        .addConsumer(ShowDiff::class.java) { showDiff -> realizeShowDiffEffect(showDiff, readingAreaContract) }
        .build()
    }

    private fun getChangedFilesTransformer(
      repositoryService: VcsRepositoryService
    ): ObservableTransformer<GetChangedFiles, ChangedFilesEvent> {
      return ObservableTransformer { getChangedFilesEvents ->
        getChangedFilesEvents
          .flatMapSingle { event -> repositoryService.getChangedFiles(event.commitId) }
          .map(::toChangedFilesEvent)
          .doOnError { logger.debug("Get changed files failed.", it) }
          .onErrorReturn { GettingChangedFilesFailed }
      }
    }

    private fun toChangedFilesEvent(changedFiles: List<ChangedFile>): ChangedFilesEvent = if (changedFiles.size == 1) {
      NoOtherFilesChanged
    } else {
      SomeMoreFilesChanged(changedFiles)
    }

    private fun realizeShowDiffEffect(
      showDiff: ShowDiff,
      readingAreaContract: ReadingAreaContract
    ) {
      val (commitId, changedFile) = showDiff
      readingAreaContract.showChangedFileDiff(commitId, changedFile)
    }
  }
}
