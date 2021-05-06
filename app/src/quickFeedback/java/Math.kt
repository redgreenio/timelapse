import java.math.BigDecimal

fun add(x: BigDecimal, y: BigDecimal): BigDecimal =
  x + y

private fun bestMatchingMethod(
  clazz: Class<*>,
  methodName: String,
  parameterTypes: List<Class<out Any>>
) = try {
  clazz.getDeclaredMethod(methodName, *parameterTypes.toTypedArray())
} catch (exception: NoSuchMethodException) {
  clazz.declaredMethods.first { it.name == methodName }
}
