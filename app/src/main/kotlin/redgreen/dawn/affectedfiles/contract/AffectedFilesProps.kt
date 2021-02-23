package redgreen.dawn.affectedfiles.contract

import io.reactivex.rxjava3.core.Observable

class AffectedFilesProps(
  val contextChanges: Observable<AffectedFileContext>,
)
