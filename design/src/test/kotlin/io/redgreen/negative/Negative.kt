package io.redgreen.negative

import org.junit.jupiter.api.fail
import java.lang.reflect.Method

fun failOnUnexpectedCallback(stackTraceMethodCallIndex: Int, vararg params: Any) {
  val stackTraceElement = Throwable().stackTrace[stackTraceMethodCallIndex]
  fail { "Unexpected call to `${getMethodSignature(stackTraceElement, params)}`" }
}

private fun getMethodSignature(
  stackTraceElement: StackTraceElement,
  params: Array<out Any>
): String {
  val clazz = Class.forName(stackTraceElement.className)
  val parameterTypes = params.map { it::class.java }.toTypedArray()
  val declaredMethod = clazz.getDeclaredMethod(stackTraceElement.methodName, *parameterTypes)
  val parameterList = getParameters(declaredMethod)

  return "${declaredMethod.name}($parameterList)"
}

private fun getParameters(method: Method): String {
  return method
    .parameters
    .map { it.type.simpleName }
    .joinToString() { type -> type }
}
