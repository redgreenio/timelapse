package toys.affectedfiles

import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.redgreen.liftoff.LiftOff
import io.redgreen.timelapse.affectedfiles.contract.AffectedFileContext
import io.redgreen.timelapse.affectedfiles.contract.AffectedFilesProps
import io.redgreen.timelapse.affectedfiles.view.javafx.AffectedFilesEntryPointPane
import javafx.application.Application
import javafx.geometry.Dimension2D
import javafx.scene.layout.Region

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
