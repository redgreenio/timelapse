package io.redgreen.intellij

import com.google.common.truth.Truth.assertThat
import com.intellij.openapi.vfs.VirtualFile
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

  @Nested
  inner class Parent {
    @Test
    fun `it should return no parent for a file in the root directory`() {
      val virtualFileInRoot = FakeVirtualFile.fileFromPath("Car.kt")

      assertThat(virtualFileInRoot.parent)
        .isNull()
    }

    @Test
    fun `it should return the parent directory for a file in a subdirectory`() {
      // given
      val virtualFileInSubdirectory = FakeVirtualFile
        .fileFromPath("io/redgreen/intellij/fs/FakeVirtualFile.kt")

      // when
      val virtualFile = virtualFileInSubdirectory.parent!!

      // then
      assertThat(virtualFile.isDirectory)
        .isTrue()
      assertThat(virtualFile.path)
        .isEqualTo("io/redgreen/intellij/fs")
    }

    @Test
    fun `parent directories should return their children`() {
      // given
      val virtualFiles = listOf(
        FakeVirtualFile.fileFromPath("io/redgreen/intellij/fs/AnotherVirtualFile.kt"),
        FakeVirtualFile.fileFromPath("io/redgreen/intellij/fs/AnotherVirtualFileSystem.kt"),
      )
      val fakeVirtualDirectory = FakeVirtualFile.directoryWithFiles(virtualFiles)

      // when
      val parent = fakeVirtualDirectory.children.first().parent

      // then
      assertThat(parent.children.map(VirtualFile::getName))
        .containsExactly(
          "AnotherVirtualFile.kt",
          "AnotherVirtualFileSystem.kt"
        )
    }
  }

  @Nested
  inner class DirectoryWithFilesChildren {
    @Test
    fun `root directory with files`() {
      // given
      val filesInDirectory = listOf(
        FakeVirtualFile.fileFromPath("Car.kt"),
        FakeVirtualFile.fileFromPath("Boat.kt"),
      )
      val rootDirectory = FakeVirtualFile.directoryWithFiles(filesInDirectory)

      // when & then
      assertThat(rootDirectory.children.map(VirtualFile::getName))
        .containsExactly("Car.kt", "Boat.kt")
    }

    @Test
    fun `subdirectory with files`() {
      // given
      val filesInDirectory = listOf(
        FakeVirtualFile.fileFromPath("/Users/jackSparrow/Ship.kt"),
        FakeVirtualFile.fileFromPath("/Users/jackSparrow/Treasure.kt"),
      )
      val directory = FakeVirtualFile.directoryWithFiles(filesInDirectory)

      // when & then
      assertThat(directory.children.map(VirtualFile::getName))
        .containsExactly("Ship.kt", "Treasure.kt")
    }
  }

  @Nested
  inner class DirectoryWithFilesPath {
    @Test
    fun `root directory path`() {
      // given
      val filesInDirectory = listOf(
        FakeVirtualFile.fileFromPath("Car.kt"),
        FakeVirtualFile.fileFromPath("Boat.kt"),
      )
      val rootDirectory = FakeVirtualFile.directoryWithFiles(filesInDirectory)

      // when & then
      assertThat(rootDirectory.path)
        .isEqualTo("/")
    }

    @Test
    fun `subdirectory path`() {
      // given
      val filesInDirectory = listOf(
        FakeVirtualFile.fileFromPath("/Users/jackSparrow/Ship.kt"),
        FakeVirtualFile.fileFromPath("/Users/jackSparrow/Treasure.kt"),
      )
      val directory = FakeVirtualFile.directoryWithFiles(filesInDirectory)

      // when & then
      assertThat(directory.path)
        .isEqualTo("/Users/jackSparrow")
    }
  }
}
