package io.redgreen.timelapse.mobius

import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.subjects.PublishSubject

class EffectHandlerTestCase<F, E>(effectHandler: ObservableTransformer<F, E>) {
  private val incomingEffectsSubject = PublishSubject.create<F>()
  private val outgoingEffectsTestObserver = incomingEffectsSubject.compose(effectHandler).test()

  fun dispatch(effect: F) {
    incomingEffectsSubject.onNext(effect)
  }

  fun assertOutgoingEvents(vararg events: E) {
    outgoingEffectsTestObserver
        .assertValues(*events)
        .assertNoErrors()
        .assertNotComplete()
  }

  fun assertNoOutgoingEvents() {
    outgoingEffectsTestObserver
        .assertNoValues()
        .assertNoErrors()
        .assertNotComplete()
  }

  fun dispose() {
    outgoingEffectsTestObserver.dispose()
  }
}
