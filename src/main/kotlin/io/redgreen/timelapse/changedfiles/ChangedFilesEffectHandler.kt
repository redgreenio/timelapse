package io.redgreen.timelapse.changedfiles

import com.spotify.mobius.rx3.RxMobius
import io.reactivex.rxjava3.core.ObservableTransformer
import io.redgreen.timelapse.changedfiles.contracts.ReadingAreaContract
import io.redgreen.timelapse.vcs.VcsRepositoryService

class ChangedFilesEffectHandler private constructor() {
  companion object {
    fun from(
      vcsRepositoryService: VcsRepositoryService,
      readingAreaContract: ReadingAreaContract
    ): ObservableTransformer<ChangedFilesEffect, ChangedFilesEvent> {
      return RxMobius
        .subtypeEffectHandler<ChangedFilesEffect, ChangedFilesEvent>()
        .addTransformer(GetChangedFiles::class.java) { getChangedFilesEvents ->
          getChangedFilesEvents
            .flatMapSingle { vcsRepositoryService.getChangedFiles(it.commitId) }
            .map { if (it.size == 1) NoOtherFilesChanged else SomeMoreFilesChanged(changedFiles = it) }
            .onErrorReturn { GettingChangedFilesFailed }
        }
        .addConsumer(ShowDiff::class.java) { (commitId, changedFile) ->
          readingAreaContract.showDiff(commitId, changedFile)
        }
        .build()
    }
  }
}
