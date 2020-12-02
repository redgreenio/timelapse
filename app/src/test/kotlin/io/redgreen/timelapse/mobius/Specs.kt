package io.redgreen.timelapse.mobius

import com.spotify.mobius.Update
import com.spotify.mobius.test.UpdateSpec

fun <M, E, F> Update<M, E, F>.spec(): UpdateSpec<M, E, F> =
  UpdateSpec(this)
