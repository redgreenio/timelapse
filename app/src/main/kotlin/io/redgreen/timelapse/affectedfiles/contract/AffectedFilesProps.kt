package io.redgreen.timelapse.affectedfiles.contract

import io.reactivex.rxjava3.core.Observable
import io.redgreen.timelapse.git.model.AffectedFile

class AffectedFilesProps(
  val contextChanges: Observable<AffectedFileContext>,
  val onSelected: (affectedFile: AffectedFile) -> Unit
)
