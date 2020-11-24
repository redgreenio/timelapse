package io.redgreen.timelapse.mobius.annotations

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.reflect.KClass

/**
 * Used to mark events that arise from effects.
 */
@Retention(SOURCE)
@Target(CLASS)
annotation class EffectEvent(
  val effect: KClass<out Any>
)
