package io.redgreen.architecture

interface Disposer<T> {
  fun T.collect()
  fun dispose()
}
