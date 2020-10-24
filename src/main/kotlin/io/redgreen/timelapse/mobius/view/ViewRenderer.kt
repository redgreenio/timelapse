package io.redgreen.timelapse.mobius.view

interface ViewRenderer<M> {
  fun render(model: M)
}
