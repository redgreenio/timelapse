package io.redgreen.timelapse.mobius

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.CLASS

/**
 * Used to mark events that arise from UI interactions.
 */
@Retention(SOURCE)
@Target(CLASS)
annotation class UiEvent
