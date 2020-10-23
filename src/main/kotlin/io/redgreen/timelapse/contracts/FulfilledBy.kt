package io.redgreen.timelapse.contracts

import kotlin.annotation.AnnotationRetention.BINARY
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.reflect.KClass

@Target(CLASS)
@Retention(BINARY)
annotation class FulfilledBy(
  val implementor: KClass<out Any>
)
