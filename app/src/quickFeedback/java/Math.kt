fun add(x: Int, y: Int): Int =
  x + y

private fun bestMatchingMethod(
  clazz: Class<*>,
  methodName: String,
  parameterTypes: Array<Class<out Any>>
) = try {
  clazz.getDeclaredMethod(methodName, *parameterTypes)
} catch (exception: NoSuchMethodException) {
  clazz.declaredMethods.first { it.name == methodName }
}
