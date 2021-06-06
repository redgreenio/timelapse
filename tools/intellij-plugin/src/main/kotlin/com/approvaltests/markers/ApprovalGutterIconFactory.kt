package com.approvaltests.markers

import com.approvaltests.markers.GutterIcon.MISSING_MISSING
import com.approvaltests.markers.GutterIcon.MISSING_PRESENT
import com.approvaltests.markers.GutterIcon.PRESENT_EMPTY
import com.approvaltests.markers.GutterIcon.PRESENT_MISSING
import com.approvaltests.markers.GutterIcon.PRESENT_PRESENT_DIFFERENT
import com.approvaltests.markers.GutterIcon.PRESENT_PRESENT_SAME
import com.approvaltests.model.FunctionCoordinates
import com.intellij.openapi.vfs.VirtualFile

class ApprovalGutterIconFactory private constructor() {
  companion object {
    fun iconFrom(
      testFile: VirtualFile,
      coordinates: FunctionCoordinates
    ): GutterIcon {
      val testDirectory = testFile.parent
      val approvedFile = findFile(testDirectory, coordinates.bestGuessApprovedFileNamePrefix())
      val receivedFile = findFile(testDirectory, coordinates.bestGuessReceivedFileNamePrefix())

      return if (receivedFile != null && approvedFile == null) {
        PRESENT_MISSING
      } else if (approvedFile != null && receivedFile != null) {
        if (approvedFile.length == receivedFile.length) {
          if (receivedFile.inputStream.reader().readText() == approvedFile.inputStream.reader().readText()) {
            PRESENT_PRESENT_SAME
          } else {
            PRESENT_PRESENT_DIFFERENT
          }
        } else if (approvedFile.length != 0L && receivedFile.length != 0L) {
          PRESENT_PRESENT_DIFFERENT
        } else {
          PRESENT_EMPTY
        }
      } else if (approvedFile != null) {
        MISSING_PRESENT
      } else {
        MISSING_MISSING
      }
    }

    private fun findFile(
      testDirectory: VirtualFile,
      prefix: String
    ): VirtualFile? {
      return testDirectory
        .children
        .find { it.name.startsWith(prefix) }
    }
  }
}
