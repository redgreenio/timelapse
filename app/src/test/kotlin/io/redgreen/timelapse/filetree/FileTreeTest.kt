package io.redgreen.timelapse.filetree

import org.approvaltests.Approvals
import org.approvaltests.reporters.QuietReporter
import org.approvaltests.reporters.UseReporter
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@UseReporter(QuietReporter::class)
internal class FileTreeTest {
  private val rootDirectoryPath = "/Users/MightySquirrel/WorldDomination/s-ray"

  @Test
  fun `it should print an empty root directory`() {
    val fileTree = FileTree
      .create(rootDirectoryPath)

    Approvals.verify(fileTree)
  }

  @Test
  fun `it should print a single file inside the root directory`() {
    val fileTree = FileTree
      .create(rootDirectoryPath, listOf(".gitignore"))

    Approvals.verify(fileTree)
  }

  @Test
  fun `it should print multiple files inside the root directory`() {
    val filePaths = listOf(".editorconfig", ".gitignore")
    val fileTree = FileTree
      .create(rootDirectoryPath, filePaths)

    Approvals.verify(fileTree)
  }

  @Test
  fun `it should print a nested file within the root directory`() {
    val filePaths = listOf("app/build.gradle.kts", ".editorconfig", ".gitignore")
    val fileTree = FileTree
      .create(rootDirectoryPath, filePaths)

    Approvals.verify(fileTree)
  }

  @Test
  fun `it should print multiple nested files within the root directory`() {
    val filePaths = listOf("app/build.gradle.kts", "app/gradle.properties", ".editorconfig", ".gitignore")
    val fileTree = FileTree
      .create(rootDirectoryPath, filePaths)

    Approvals.verify(fileTree)
  }

  @Test
  fun `it should print multiple nested files within multiple directories`() {
    val filePaths = listOf(
      "app/build.gradle.kts",
      "app/gradle.properties",
      "buildscripts/install-hooks.gradle.kts",
      "buildscripts/quality.gradle.kts",
      ".editorconfig",
      ".gitignore"
    )
    val fileTree = FileTree
      .create(rootDirectoryPath, filePaths)

    Approvals.verify(fileTree)
  }

  @Disabled
  @Test
  fun `it should print nested files within nested directories`() {
    val filePaths = listOf(
      "app/src/main/kotlin/HelloWorld.kt",
      "app/build.gradle.kts",
      "app/gradle.properties",
      "buildscripts/install-hooks.gradle.kts",
      "buildscripts/quality.gradle.kts",
      ".editorconfig",
      ".gitignore"
    )
    val fileTree = FileTree
      .create(rootDirectoryPath, filePaths)

    Approvals.verify(fileTree)
  }
}
