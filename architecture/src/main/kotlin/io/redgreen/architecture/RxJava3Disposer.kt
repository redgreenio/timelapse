package io.redgreen.architecture

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import kotlin.LazyThreadSafetyMode.NONE

class RxJava3Disposer : Disposer<Disposable> {
  private val compositeDisposable by lazy(NONE) { CompositeDisposable() }

  override fun Disposable.collect() {
    compositeDisposable.add(this)
  }

  override fun dispose() {
    compositeDisposable.clear()
  }
}
