package com.approvaltests.model

import com.approvaltests.model.ApprovalFile.Approved
import com.approvaltests.model.ApprovalFile.Received
import com.google.common.truth.Truth.assertThat
import io.redgreen.intellij.FakeVirtualFile
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ApprovalFileTest {
  @Nested
  inner class FactoryFunction {
    @Test
    fun `create an Approved instance`() {
      assertThat(ApprovalFile.from(FakeVirtualFile("my.approved.txt")))
        .isInstanceOf(Approved::class.java)
    }

    @Test
    fun `create a Received instance`() {
      assertThat(ApprovalFile.from(FakeVirtualFile("my.received.txt")))
        .isInstanceOf(Received::class.java)
    }

    @Test
    fun `don't create an instance for files other than approved and received`() {
      assertThat(ApprovalFile.from(FakeVirtualFile("my-file.txt")))
        .isNull()
    }
  }

  @Nested
  inner class ApprovedType {
    @Test
    fun `return a Received file if it exists`() {
      // given
      val approved = Approved(FakeVirtualFile("my.approved.txt", listOf("my.received.txt")))

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
      val approved = Approved(FakeVirtualFile("my.approved.txt"))

      // when
      val counterpart = approved.counterpart()

      // then
      assertThat(counterpart)
        .isNull()
    }
  }
}
