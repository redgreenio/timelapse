package com.approvaltests.model

import com.approvaltests.model.ApprovalFile.Approved
import com.approvaltests.model.ApprovalFile.Received
import com.google.common.truth.Truth.assertThat
import com.intellij.openapi.vfs.VirtualFile
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ApprovalFileTest {
  @Nested
  inner class FactoryFunction {
    @Test
    fun `create an Approved instance`() {
      // given
      val virtualFile = mock<VirtualFile>().apply { whenever(this.name).thenReturn("my.approved.txt") }

      // when & then
      assertThat(ApprovalFile.from(virtualFile))
        .isInstanceOf(Approved::class.java)
    }

    @Test
    fun `create a Received instance`() {
      // given
      val virtualFile = mock<VirtualFile>().apply { whenever(this.name).thenReturn("my.received.txt") }

      // when & then
      assertThat(ApprovalFile.from(virtualFile))
        .isInstanceOf(Received::class.java)
    }

    @Test
    fun `don't create an instance for files other than approved and received`() {
      // given
      val virtualFile = mock<VirtualFile>().apply { whenever(this.name).thenReturn("my-file.txt") }

      // when & then
      assertThat(ApprovalFile.from(virtualFile))
        .isNull()
    }
  }
}
