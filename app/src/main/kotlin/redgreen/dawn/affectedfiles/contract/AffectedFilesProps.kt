package redgreen.dawn.affectedfiles.contract

import io.reactivex.rxjava3.core.Observable
import redgreen.dawn.affectedfiles.view.model.AffectedFileCellViewModel

class AffectedFilesProps(
  val contextChanges: Observable<AffectedFileContext>,
  val viewModelChanges: Observable<List<AffectedFileCellViewModel>>,
  val fileSelectedCallback: (filePath: String) -> Unit
)
