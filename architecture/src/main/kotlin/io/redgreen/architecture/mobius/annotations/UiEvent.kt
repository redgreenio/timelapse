package io.redgreen.architecture.mobius.annotations

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.CLASS

/**
 * Used to mark events that arise from UI interactions.
 */
@Retention(SOURCE)
@Target(CLASS)
annotation class UiEvent
