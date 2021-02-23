package redgreen.dawn.architecture

interface Disposer<T> {
  fun T.collect()
  fun dispose()
}
