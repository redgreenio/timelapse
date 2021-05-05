package io.redgreen.design.text

import io.redgreen.negative.failOnUnexpectedCallback

open class CrashAndBurnOnUnexpectedCallbackVisitor : StyledTextVisitor {
  companion object {
    private const val CALL_STACK_INDEX_OF_CALLBACK_METHOD = 1
  }

  protected val contentBuilder = StringBuilder()

  val content: String
    get() = contentBuilder.toString()

  override fun onEnterLine(lineNumber: Int) {
    failOnUnexpectedCallback(CALL_STACK_INDEX_OF_CALLBACK_METHOD)
  }

  override fun onEnterLine(lineNumber: Int, style: LineStyle) {
    failOnUnexpectedCallback(CALL_STACK_INDEX_OF_CALLBACK_METHOD)
  }

  override fun onExitLine(lineNumber: Int) {
    failOnUnexpectedCallback(CALL_STACK_INDEX_OF_CALLBACK_METHOD)
  }

  override fun onExitLine(lineNumber: Int, style: LineStyle) {
    failOnUnexpectedCallback(CALL_STACK_INDEX_OF_CALLBACK_METHOD)
  }

  override fun onText(text: String) {
    failOnUnexpectedCallback(CALL_STACK_INDEX_OF_CALLBACK_METHOD)
  }

  override fun onBeginStyle(textStyle: TextStyle) {
    failOnUnexpectedCallback(CALL_STACK_INDEX_OF_CALLBACK_METHOD)
  }

  override fun onEndStyle(textStyle: TextStyle) {
    failOnUnexpectedCallback(CALL_STACK_INDEX_OF_CALLBACK_METHOD)
  }
}
