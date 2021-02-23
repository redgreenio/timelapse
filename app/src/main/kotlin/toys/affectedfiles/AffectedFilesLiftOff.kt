package toys.affectedfiles

import io.reactivex.rxjava3.subjects.PublishSubject
import javafx.application.Application
import javafx.geometry.Dimension2D
import redgreen.dawn.affectedfiles.contract.AffectedFilesProps
import redgreen.dawn.affectedfiles.view.javafx.AffectedFilesEntryPointPane
import toys.lliftoff.LiftOff

class AffectedFilesLiftOff : LiftOff<AffectedFilesProps, AffectedFilesEntryPointPane>() {
  override fun title(): String =
    "Affected files"

  override fun howBig(): Dimension2D =
    Dimension2D(500.0, 600.0)

  override fun entryPoint(): AffectedFilesEntryPointPane =
    AffectedFilesEntryPointPane()

  override fun props(): AffectedFilesProps =
    AffectedFilesProps(PublishSubject.create())
}

fun main() {
  Application.launch(AffectedFilesLiftOff::class.java)
}
