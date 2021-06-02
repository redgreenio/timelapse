package com.approvaltests.model

import com.approvaltests.model.ApprovalFile.Approved
import com.approvaltests.model.ApprovalFile.Received
import com.google.common.truth.Truth.assertThat
import io.redgreen.intellij.TestVirtualFile
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ApprovalFileTest {
  @Nested
  inner class FactoryFunction {
    @Test
    fun `create an Approved instance`() {
      assertThat(ApprovalFile.from(TestVirtualFile("my.approved.txt")))
        .isInstanceOf(Approved::class.java)
    }

    @Test
    fun `create a Received instance`() {
      assertThat(ApprovalFile.from(TestVirtualFile("my.received.txt")))
        .isInstanceOf(Received::class.java)
    }

    @Test
    fun `don't create an instance for files other than approved and received`() {
      assertThat(ApprovalFile.from(TestVirtualFile("my-file.txt")))
        .isNull()
    }
  }
}
