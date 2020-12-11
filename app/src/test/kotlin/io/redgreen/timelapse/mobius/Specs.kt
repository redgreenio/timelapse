package io.redgreen.timelapse.mobius

import com.spotify.mobius.Init
import com.spotify.mobius.Update
import com.spotify.mobius.test.InitSpec
import com.spotify.mobius.test.UpdateSpec
import io.reactivex.rxjava3.core.ObservableTransformer

fun <M, E, F> Update<M, E, F>.spec(): UpdateSpec<M, E, F> =
  UpdateSpec(this)

fun <M, F> Init<M, F>.spec(): InitSpec<M, F> =
  InitSpec(this)

fun <F, E> ObservableTransformer<F, E>.testCase(): EffectHandlerTestCase<F, E> =
  EffectHandlerTestCase(this)
