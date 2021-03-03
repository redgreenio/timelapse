package io.redgreen.architecture.mobius.view

interface ViewRenderer<M> {
  fun render(model: M)
}
