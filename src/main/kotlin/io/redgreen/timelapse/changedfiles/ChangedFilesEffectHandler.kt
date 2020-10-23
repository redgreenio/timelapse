package io.redgreen.timelapse.changedfiles

import com.spotify.mobius.rx3.RxMobius
import io.reactivex.rxjava3.core.ObservableTransformer
import io.redgreen.timelapse.domain.VcsRepositoryService

class ChangedFilesEffectHandler private constructor() {
  companion object {
    fun from(vcsRepositoryService: VcsRepositoryService): ObservableTransformer<ChangedFilesEffect, ChangedFilesEvent> {
      return RxMobius
        .subtypeEffectHandler<ChangedFilesEffect, ChangedFilesEvent>()
        .addTransformer(GetChangedFiles::class.java) { upstream ->
          upstream.flatMapSingle { vcsRepositoryService.getChangedFilePaths(it.commitId) }
            .map { SomeMoreFilesChanged(it) }
        }
        .build()
    }
  }
}
