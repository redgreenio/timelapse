package io.redgreen.timelapse.contentviewer

import com.spotify.mobius.rx3.RxMobius
import io.reactivex.rxjava3.core.ObservableTransformer
import io.redgreen.timelapse.platform.ClipboardService
import io.redgreen.timelapse.platform.SchedulersProvider
import io.redgreen.timelapse.vcs.VcsRepositoryService
import org.slf4j.LoggerFactory

class ContentViewerEffectHandler private constructor() {
  companion object {
    private val logger = LoggerFactory.getLogger(ContentViewerEffectHandler::class.java)

    fun from(
      vcsRepositoryService: VcsRepositoryService,
      clipboardService: ClipboardService,
      schedulersProvider: SchedulersProvider
    ): ObservableTransformer<ContentViewerEffect, ContentViewerEvent> {
      return RxMobius
        .subtypeEffectHandler<ContentViewerEffect, ContentViewerEvent>()
        .addTransformer(LoadBlobDiff::class.java, loadBlobDiffTransformer(vcsRepositoryService))
        .addTransformer(LoadBlobDiffInformation::class.java, loadBlobDiffInformation(vcsRepositoryService))
        .addConsumer(
          CopyCommitIdToClipboard::class.java,
          { clipboardService.copy(it.commitId) },
          schedulersProvider.ui()
        )
        .build()
    }

    private fun loadBlobDiffTransformer(
      vcsRepositoryService: VcsRepositoryService
    ): ObservableTransformer<LoadBlobDiff, ContentViewerEvent> {
      return ObservableTransformer { loadBlobDiffEvents ->
        loadBlobDiffEvents
          .flatMapSingle { vcsRepositoryService.getBlobDiff(it.selectedFilePath, it.commitId) }
          .map<ContentViewerEvent> { BlobDiffLoaded(it) }
          .doOnError { logger.error("Load blob diff failed.", it) }
          .onErrorReturn { UnableToLoadBlobDiff }
      }
    }

    private fun loadBlobDiffInformation(
      vcsRepositoryService: VcsRepositoryService
    ): ObservableTransformer<LoadBlobDiffInformation, ContentViewerEvent> {
      return ObservableTransformer { loadBlobDiffEvents ->
        loadBlobDiffEvents
          .flatMapSingle { vcsRepositoryService.getBlobDiffInformation(it.selectedFilePath, it.commitId) }
          .map<ContentViewerEvent> { BlobDiffInformationLoaded(it) }
          .doOnError { logger.error("Load blob diff information failed.", it) }
          .onErrorReturn { UnableToLoadBlobDiffInformation }
      }
    }
  }
}
