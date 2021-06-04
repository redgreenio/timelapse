package com.approvaltests.model

class FunctionCoordinates private constructor(
  private val functionName: String,
  private val containingClass: String
) {
  companion object {
    fun from(
      functionName: String,
      containingClass: String
    ): FunctionCoordinates {
      return FunctionCoordinates(functionName, containingClass)
    }
  }

  fun bestGuessApprovedFileName(): String {
    return listOf(containingClass, functionName, "approved", "txt").joinToString(".")
  }

  fun bestGuessReceivedFileName(): String {
    return listOf(containingClass, functionName, "received", "txt").joinToString(".")
  }
}
