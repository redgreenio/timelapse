package com.approvaltests.markers

import com.approvaltests.markers.GutterIcon.MISSING_MISSING
import com.approvaltests.markers.GutterIcon.MISSING_PRESENT
import com.approvaltests.markers.GutterIcon.PRESENT_EMPTY
import com.approvaltests.markers.GutterIcon.PRESENT_MISSING
import com.approvaltests.markers.GutterIcon.PRESENT_PRESENT_DIFFERENT
import com.approvaltests.markers.GutterIcon.PRESENT_PRESENT_SAME
import com.approvaltests.model.FunctionCoordinates
import com.google.common.truth.Truth.assertThat
import io.redgreen.intellij.FakeVirtualFile
import org.junit.jupiter.api.Test

class ApprovalGutterIconFactoryTest {
  private val testFile = FakeVirtualFile.file("test/kotlin/CanaryTest.kt")
  private val approvedFile = FakeVirtualFile.file("test/kotlin/CanaryTest.approvals is setup.approved.txt")
  private val receivedFile = FakeVirtualFile.file("test/kotlin/CanaryTest.approvals is setup.received.txt")

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
  fun `received missing, approved present (empty)`() {
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
  fun `received missing, approved present (non-empty)`() {
    // given
    val approved = FakeVirtualFile
      .file("test/kotlin/CanaryTest.approvals is setup.approved.kt", "Have a blast!")
    FakeVirtualFile.directoryWithFiles(listOf(testFile, approved))

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
    val received = FakeVirtualFile
      .file("test/kotlin/CanaryTest.approvals is setup.received.kt", "Hello, world!")
    val approved = FakeVirtualFile
      .file("test/kotlin/CanaryTest.approvals is setup.approved.kt")
    FakeVirtualFile.directoryWithFiles(listOf(testFile, received, approved))

    // when
    val gutterIcon = ApprovalGutterIconFactory
      .iconFrom(testFile, functionCoordinates)

    // then
    assertThat(gutterIcon)
      .isEqualTo(PRESENT_EMPTY)
  }

  @Test
  fun `received present, approved present (contents same)`() {
    // given
    val received = FakeVirtualFile
      .file("test/kotlin/CanaryTest.approvals is setup.received.kt", "Hello, world!")
    val approved = FakeVirtualFile
      .file("test/kotlin/CanaryTest.approvals is setup.approved.kt", "Hello, world!")
    FakeVirtualFile.directoryWithFiles(listOf(testFile, received, approved))

    // when
    val gutterIcon = ApprovalGutterIconFactory
      .iconFrom(testFile, functionCoordinates)

    // then
    assertThat(gutterIcon)
      .isEqualTo(PRESENT_PRESENT_SAME)
  }

  @Test
  fun `received present, approved present (equal length, contents different)`() {
    // given
    val received = FakeVirtualFile
      .file("test/kotlin/CanaryTest.approvals is setup.received.kt", "One")
    val approved = FakeVirtualFile
      .file("test/kotlin/CanaryTest.approvals is setup.approved.kt", "Two")
    FakeVirtualFile.directoryWithFiles(listOf(testFile, received, approved))

    // when
    val gutterIcon = ApprovalGutterIconFactory
      .iconFrom(testFile, functionCoordinates)

    // then
    assertThat(gutterIcon)
      .isEqualTo(PRESENT_PRESENT_DIFFERENT)
  }

  @Test
  fun `received present, approved present (unequal length)`() {
    // given
    val received = FakeVirtualFile
      .file("test/kotlin/CanaryTest.approvals is setup.received.kt", "One")
    val approved = FakeVirtualFile
      .file("test/kotlin/CanaryTest.approvals is setup.approved.kt", "On")
    FakeVirtualFile.directoryWithFiles(listOf(testFile, received, approved))

    // when
    val gutterIcon = ApprovalGutterIconFactory
      .iconFrom(testFile, functionCoordinates)

    // then
    assertThat(gutterIcon)
      .isEqualTo(PRESENT_PRESENT_DIFFERENT)
  }
}
