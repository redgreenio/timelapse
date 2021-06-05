package com.approvaltests.model

import com.approvaltests.model.ApprovalFile.Approved
import com.approvaltests.model.ApprovalFile.Received
import com.google.common.truth.Truth.assertThat
import io.redgreen.intellij.FakeVirtualFile
import io.redgreen.intellij.OldFakeVirtualFile
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ApprovalFileTest {
  @Nested
  inner class FactoryFunction {
    @Test
    fun `create an Approved instance`() {
      assertThat(ApprovalFile.from(FakeVirtualFile.fileFromPath("my.approved.txt")))
        .isInstanceOf(Approved::class.java)
    }

    @Test
    fun `create a Received instance`() {
      assertThat(ApprovalFile.from(FakeVirtualFile.fileFromPath("my.received.txt")))
        .isInstanceOf(Received::class.java)
    }

    @Test
    fun `don't create an instance for files other than approved and received`() {
      assertThat(ApprovalFile.from(FakeVirtualFile.fileFromPath("my-file.txt")))
        .isNull()
    }
  }

  @Nested
  inner class ApprovedType {
    @Test
    fun `return a Received file if it exists`() {
      // given
      val approved = ApprovalFile.from(OldFakeVirtualFile("my.approved.txt", listOf("my.received.txt")))!!

      // when
      val counterpart = approved.counterpart()

      // then
      assertThat(counterpart)
        .isInstanceOf(Received::class.java)
      assertThat((counterpart as Received).virtualFile.name)
        .isEqualTo("my.received.txt")
    }

    @Test
    fun `return null if the received file does not exist`() {
      // given
      val approved = ApprovalFile.from(FakeVirtualFile.fileFromPath("my.approved.txt"))!!

      // when
      val counterpart = approved.counterpart()

      // then
      assertThat(counterpart)
        .isNull()
    }
  }

  @Nested
  inner class ReceivedType {
    @Test
    fun `return a Approved file if it exists`() {
      // given
      val received = ApprovalFile.from(OldFakeVirtualFile("my.received.txt", listOf("my.approved.txt")))!!

      // when
      val counterpart = received.counterpart()

      // then
      assertThat(counterpart)
        .isInstanceOf(Approved::class.java)
      assertThat((counterpart as Approved).virtualFile.name)
        .isEqualTo("my.approved.txt")
    }

    @Test
    fun `return null if the approved file does not exist`() {
      // given
      val received = ApprovalFile.from(FakeVirtualFile.fileFromPath("my.received.txt"))!!

      // when
      val counterpart = received.counterpart()

      // then
      assertThat(counterpart)
        .isNull()
    }

    @Test
    fun `file name of the approved file`() {
      // given
      val received = ApprovalFile.from(FakeVirtualFile.fileFromPath("my.fancy.received.file.txt"))!! as Received

      // when & then
      assertThat(received.approvedFileName)
        .isEqualTo("my.fancy.approved.file.txt")
    }
  }
}
