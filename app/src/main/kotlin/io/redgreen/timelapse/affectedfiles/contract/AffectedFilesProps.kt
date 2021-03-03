package io.redgreen.timelapse.affectedfiles.contract

import io.reactivex.rxjava3.core.Observable

class AffectedFilesProps(
  val contextChanges: Observable<AffectedFileContext>,
  val fileSelectedCallback: (filePath: String) -> Unit
)
