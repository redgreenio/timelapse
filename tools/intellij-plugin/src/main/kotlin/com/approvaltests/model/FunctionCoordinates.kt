package com.approvaltests.model

data class FunctionCoordinates private constructor(
  private val identifier: String,
  private val containingClasses: List<String>
) {
  companion object {
    private const val DOT = "."
    private const val APPROVED = "approved"
    private const val RECEIVED = "received"

    fun from(
      functionName: String,
      containingClass: String,
      vararg containingClasses: String
    ): FunctionCoordinates {
      return FunctionCoordinates(functionName, listOf(*containingClasses, containingClass))
    }
  }

  fun bestGuessApprovedFileNamePrefix(): String =
    fileNameFor(APPROVED)

  fun bestGuessReceivedFileNamePrefix(): String =
    fileNameFor(RECEIVED)

  private fun fileNameFor(approvalFileType: String): String {
    val listOf = containingClasses + listOf(identifier, approvalFileType)
    return listOf.joinToString(DOT) + DOT
  }
}
