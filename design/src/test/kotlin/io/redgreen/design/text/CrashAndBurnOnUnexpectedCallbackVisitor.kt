package io.redgreen.design.text

import org.junit.jupiter.api.fail

open class CrashAndBurnOnUnexpectedCallbackVisitor : StyledTextVisitor {
  companion object {
    private const val CALL_STACK_INDEX_OF_CALLBACK_METHOD = 2
  }

  override fun onEnterLine(lineNumber: Int) {
    failOnUnexpectedCallback()
  }

  override fun onEnterLine(lineNumber: Int, style: LineStyle) {
    failOnUnexpectedCallback()
  }

  override fun onExitLine(lineNumber: Int) {
    failOnUnexpectedCallback()
  }

  override fun onExitLine(lineNumber: Int, style: LineStyle) {
    failOnUnexpectedCallback()
  }

  private fun failOnUnexpectedCallback() {
    val methodName = getMethodName()
    fail { "Unexpected call to `$methodName`" }
  }

  private fun getMethodName(): String =
    Throwable().stackTrace[CALL_STACK_INDEX_OF_CALLBACK_METHOD].methodName
}
