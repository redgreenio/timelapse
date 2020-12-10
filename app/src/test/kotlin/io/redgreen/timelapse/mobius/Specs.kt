package io.redgreen.timelapse.mobius

import com.spotify.mobius.Init
import com.spotify.mobius.Update
import com.spotify.mobius.test.InitSpec
import com.spotify.mobius.test.UpdateSpec

fun <M, E, F> Update<M, E, F>.spec(): UpdateSpec<M, E, F> =
  UpdateSpec(this)

fun <M, F> Init<M, F>.spec(): InitSpec<M, F> =
  InitSpec(this)
