package io.redgreen.timelapse.changedfiles

import com.spotify.mobius.rx3.RxMobius
import io.reactivex.rxjava3.core.ObservableTransformer
import io.redgreen.timelapse.changedfiles.contracts.ReadingAreaContract
import io.redgreen.timelapse.domain.VcsRepositoryService

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
            .flatMapSingle { vcsRepositoryService.getChangedFilePaths(it.commitId) }
            .map { if (it.size == 1) NoOtherFilesChanged else SomeMoreFilesChanged(it) }
            .onErrorReturn { GettingChangedFilesFailed }
        }
        .addConsumer(ShowDiff::class.java) { (commitId, filePath) ->
          readingAreaContract.showDiff(commitId, filePath)
        }
        .build()
    }
  }
}
