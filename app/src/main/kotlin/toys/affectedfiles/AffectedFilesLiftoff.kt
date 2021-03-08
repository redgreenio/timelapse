package toys.affectedfiles

import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.redgreen.liftoff.Liftoff
import io.redgreen.timelapse.affectedfiles.contract.AffectedFileContext
import io.redgreen.timelapse.affectedfiles.contract.AffectedFilesProps
import io.redgreen.timelapse.affectedfiles.usecases.GetAffectedFilesUseCase
import io.redgreen.timelapse.affectedfiles.view.javafx.AffectedFilesEntryPointPane
import io.redgreen.timelapse.foo.fastLazy
import io.redgreen.timelapse.metrics.GetCommitsMetric
import io.redgreen.timelapse.metrics.GetTrackedFilesMetric
import javafx.application.Application
import javafx.geometry.Dimension2D
import javafx.scene.layout.Region
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

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

  override val propsUi: Region by fastLazy { AffectedFilesPropsUi(contextChanges) }

  override val liftoffTitle: String = TITLE

  override val howBig: Dimension2D = Dimension2D(WIDTH, HEIGHT)

  init {
    EventBus.getDefault().register(this)
  }

  @Subscribe
  fun subscribe(metric: GetTrackedFilesMetric) {
    println("Tracked files: ${metric.duration.toMillis()}ms.")
  }

  @Subscribe
  fun subscribe(metric: GetCommitsMetric) {
    println("Get commits: ${metric.duration.toMillis()}ms.")
  }

  @Subscribe
  fun subscribe(metric: GetAffectedFilesUseCase.Metric) {
    println("Affected files: ${metric.duration.toMillis()}ms.")
  }
}
