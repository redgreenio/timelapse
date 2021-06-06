package com.approvaltests.markers

import com.approvaltests.markers.GutterIcon.MISSING_MISSING
import com.approvaltests.markers.GutterIcon.MISSING_PRESENT
import com.approvaltests.markers.GutterIcon.PRESENT_EMPTY
import com.approvaltests.markers.GutterIcon.PRESENT_MISSING
import com.approvaltests.model.FunctionCoordinates
import com.google.common.truth.Truth.assertThat
import io.redgreen.intellij.FakeVirtualFile
import org.junit.jupiter.api.Test

class ApprovalGutterIconFactoryTest {
  private val testFile = FakeVirtualFile.fileFromPath("test/kotlin/CanaryTest.kt")
  private val approvedFile = FakeVirtualFile.fileFromPath("test/kotlin/CanaryTest.approvals is setup.approved.txt")
  private val receivedFile = FakeVirtualFile.fileFromPath("test/kotlin/CanaryTest.approvals is setup.received.txt")

  private val functionCoordinates = FunctionCoordinates.from("approvals is setup", "CanaryTest")

  @Test
  fun `received missing, approved missing`() {
    // when
    val gutterIcon = ApprovalGutterIconFactory
      .iconFrom(testFile, functionCoordinates)

    // then
    assertThat(gutterIcon)
      .isEqualTo(MISSING_MISSING)
  }

  @Test
  fun `received missing, approved present`() {
    // given
    FakeVirtualFile.directoryWithFiles(listOf(testFile, approvedFile))

    // when
    val gutterIcon = ApprovalGutterIconFactory
      .iconFrom(testFile, functionCoordinates)

    // then
    assertThat(gutterIcon)
      .isEqualTo(MISSING_PRESENT)
  }

  @Test
  fun `received present, approved missing`() {
    // given
    FakeVirtualFile.directoryWithFiles(listOf(testFile, receivedFile))

    // when
    val gutterIcon = ApprovalGutterIconFactory
      .iconFrom(testFile, functionCoordinates)

    // then
    assertThat(gutterIcon)
      .isEqualTo(PRESENT_MISSING)
  }

  @Test
  fun `received present, approved empty`() {
    // given
    FakeVirtualFile.directoryWithFiles(listOf(testFile, receivedFile, approvedFile))

    // when
    val gutterIcon = ApprovalGutterIconFactory
      .iconFrom(testFile, functionCoordinates)

    // then
    assertThat(gutterIcon)
      .isEqualTo(PRESENT_EMPTY)
  }
}
