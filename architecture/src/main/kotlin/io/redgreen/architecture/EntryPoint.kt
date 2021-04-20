package io.redgreen.architecture

interface EntryPoint<PROPS : Any> {
  fun mount(props: PROPS)
  fun unmount()
}
