package io.redgreen.negative

import org.junit.jupiter.api.fail
import java.lang.reflect.Method

fun failOnInvocation(stackTraceMethodCallIndex: Int, vararg params: Any) {
  val stackTraceElement = Throwable().stackTrace[stackTraceMethodCallIndex]
  fail { "Unexpected call to `${getMethodSignature(stackTraceElement, params)}`" }
}

private fun getMethodSignature(
  stackTraceElement: StackTraceElement,
  params: Array<out Any>
): String {
  val clazz = Class.forName(stackTraceElement.className)
  val methodName = stackTraceElement.methodName
  val parameterTypes = params.map { it::class.java }.toTypedArray()
  val declaredMethod = bestMatchingMethod(clazz, methodName, parameterTypes)
  val parameterList = getParameters(declaredMethod)

  return "${declaredMethod.name}($parameterList)"
}

private fun bestMatchingMethod(
  clazz: Class<*>,
  methodName: String,
  parameterTypes: Array<Class<out Any>>
) = try {
  clazz.getDeclaredMethod(methodName, *parameterTypes)
} catch (exception: NoSuchMethodException) {
  clazz.declaredMethods.first { it.name == methodName }
}

private fun getParameters(method: Method): String {
  return method
    .parameters
    .map { it.type.simpleName }
    .joinToString() { type -> type }
}
