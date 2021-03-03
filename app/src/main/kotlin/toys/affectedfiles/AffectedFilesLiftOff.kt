package toys.affectedfiles

import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.redgreen.liftoff.LiftOff
import javafx.application.Application
import javafx.geometry.Dimension2D
import javafx.scene.layout.Region
import redgreen.dawn.affectedfiles.contract.AffectedFileContext
import redgreen.dawn.affectedfiles.contract.AffectedFilesProps
import redgreen.dawn.affectedfiles.view.javafx.AffectedFilesEntryPointPane

fun main() {
  Application.launch(AffectedFilesLiftOff::class.java)
}

class AffectedFilesLiftOff : LiftOff<AffectedFilesProps, AffectedFilesEntryPointPane>() {
  private companion object {
    private const val TITLE = "Affected files"
    private const val WIDTH = 500.0
    private const val HEIGHT = 600.0
  }

  private val contextChanges = BehaviorSubject.create<AffectedFileContext>()
  private val affectedFilesPropsUi = AffectedFilesPropsUi(contextChanges)

  override fun title(): String = TITLE

  override fun howBig(): Dimension2D =
    Dimension2D(WIDTH, HEIGHT)

  override fun entryPoint(): AffectedFilesEntryPointPane =
    AffectedFilesEntryPointPane()

  override fun props(): AffectedFilesProps =
    AffectedFilesProps(contextChanges) { affectedFilesPropsUi.showAffectedFile(it) }

  override fun propsUi(): Region =
    AffectedFilesPropsUi(contextChanges)
}
