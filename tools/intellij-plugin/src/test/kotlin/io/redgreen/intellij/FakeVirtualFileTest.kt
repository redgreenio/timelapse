package io.redgreen.intellij

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class FakeVirtualFileTest {
  @Nested
  inner class FileName {
    @Test
    fun `it should return the name of a file in the root directory`() {
      val virtualFileInRoot = FakeVirtualFile.fileFromPath("Car.kt")

      assertThat(virtualFileInRoot.name)
        .isEqualTo("Car.kt")
    }

    @Test
    fun `it should return the name of a file from a subdirectory`() {
      val virtualFileInSubdirectory = FakeVirtualFile
        .fileFromPath("io/redgreen/intellij/fs/FakeVirtualFile.kt")

      assertThat(virtualFileInSubdirectory.name)
        .isEqualTo("FakeVirtualFile.kt")
    }
  }
}
