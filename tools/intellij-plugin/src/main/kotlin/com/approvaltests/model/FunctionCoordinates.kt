package com.approvaltests.model

class FunctionCoordinates private constructor(
  private val identifier: String,
  private val containingClasses: List<String>
) {
  companion object {
    fun from(
      functionName: String,
      containingClass: String,
      vararg containingClasses: String
    ): FunctionCoordinates {
      return FunctionCoordinates(functionName, listOf(*containingClasses, containingClass))
    }
  }

  fun bestGuessApprovedFileName(): String =
    fileNameFor("approved")

  fun bestGuessReceivedFileName(): String =
    fileNameFor("received")

  private fun fileNameFor(approvalFileType: String): String {
    val listOf = containingClasses + listOf(identifier, approvalFileType, "txt")
    return listOf.joinToString(".")
  }
}
