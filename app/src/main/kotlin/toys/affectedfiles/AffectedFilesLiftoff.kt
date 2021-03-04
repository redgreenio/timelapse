package toys.affectedfiles

import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.redgreen.liftoff.Liftoff
import io.redgreen.timelapse.affectedfiles.contract.AffectedFileContext
import io.redgreen.timelapse.affectedfiles.contract.AffectedFilesProps
import io.redgreen.timelapse.affectedfiles.view.javafx.AffectedFilesEntryPointPane
import io.redgreen.timelapse.foo.fastLazy
import javafx.application.Application
import javafx.geometry.Dimension2D
import javafx.scene.layout.Region

fun main() {
  Application.launch(AffectedFilesLiftoff::class.java)
}

class AffectedFilesLiftoff : Liftoff<AffectedFilesProps, AffectedFilesEntryPointPane>() {
  private companion object {
    private const val TITLE = "Affected files"
    private const val WIDTH = 500.0
    private const val HEIGHT = 600.0
  }

  private val contextChanges = BehaviorSubject.create<AffectedFileContext>()

  override val entryPoint: AffectedFilesEntryPointPane by fastLazy { AffectedFilesEntryPointPane() }

  override val props: AffectedFilesProps by fastLazy {
    AffectedFilesProps(contextChanges) { (propsUi as AffectedFilesPropsUi).showAffectedFile(it) }
  }

  override val propsUi: Region by fastLazy {
    AffectedFilesPropsUi(contextChanges)
  }

  override fun title(): String = TITLE

  override fun howBig(): Dimension2D =
    Dimension2D(WIDTH, HEIGHT)
}
