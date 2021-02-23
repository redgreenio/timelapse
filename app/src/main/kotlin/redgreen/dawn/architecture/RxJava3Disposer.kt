package redgreen.dawn.architecture

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.redgreen.timelapse.foo.fastLazy

class RxJava3Disposer : Disposer<Disposable> {
  private val compositeDisposable by fastLazy { CompositeDisposable() }

  override fun Disposable.collect() {
    compositeDisposable.add(this)
  }

  override fun dispose() {
    compositeDisposable.clear()
  }
}
