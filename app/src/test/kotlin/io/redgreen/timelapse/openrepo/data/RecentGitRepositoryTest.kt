package io.redgreen.timelapse.openrepo.data

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class RecentGitRepositoryTest {
  private val userHome = "/Users/goushik"
  private val userHomeOnWindows = """C:\Users\goushik"""

  @Test
  @DisabledOnOs(OS.WINDOWS)
  fun `it should use the name of the leaf directory as title`() {
    val path = "$userHome/PyCharmProjects/django/.git"
    assertThat(RecentGitRepository(path).title())
      .isEqualTo("django")
  }

  @Test
  @EnabledOnOs(OS.WINDOWS)
  fun `it should use the name of the leaf directory as title (windows)`() {
    val path = """$userHomeOnWindows\PyCharmProjects\django\.git"""
    assertThat(RecentGitRepository(path).title())
      .isEqualTo("django")
  }

  @Test
  @DisabledOnOs(OS.WINDOWS)
  fun `it should return the exact path of directory if it is not present in the user's home directory`() {
    val path = "/PyCharmProjects/django/.git"
    val expectedSubtitle = "/PyCharmProjects/django"

    assertThat(RecentGitRepository(path).subtitle(userHome))
      .isEqualTo(expectedSubtitle)
  }

  @Test
  @EnabledOnOs(OS.WINDOWS)
  fun `it should return the exact path of directory if it is not present in the user's home directory (windows)`() {
    val path = """C:\PyCharmProjects\django\.git"""
    val expectedSubtitle = """C:\PyCharmProjects\django"""

    assertThat(RecentGitRepository(path).subtitle(userHomeOnWindows))
      .isEqualTo(expectedSubtitle)
  }

  @Test
  @DisabledOnOs(OS.WINDOWS)
  fun `it should replace the user's home directory with a ~ in the subtitle`() {
    val path = "$userHome/PyCharmProjects/django/.git"
    assertThat(RecentGitRepository(path).subtitle(userHome))
      .isEqualTo("~/PyCharmProjects/django")
  }

  @Test
  @EnabledOnOs(OS.WINDOWS)
  fun `it should replace the user's home directory with a ~ in the subtitle (windows)`() {
    val path = """$userHomeOnWindows\PyCharmProjects\django\.git"""
    assertThat(RecentGitRepository(path).subtitle(userHomeOnWindows))
      .isEqualTo("""~\PyCharmProjects\django""")
  }

  @Test
  @DisabledOnOs(OS.WINDOWS)
  fun `it should replace the user's home directory (ending with a trailing slash) with a ~ in the subtitle`() {
    val userHome = "/Users/goushik/"
    val path = "${userHome}PyCharmProjects/django/.git"

    assertThat(RecentGitRepository(path).subtitle(userHome))
      .isEqualTo("~/PyCharmProjects/django")
  }

  @Test
  @EnabledOnOs(OS.WINDOWS)
  fun `it should replace the user's home directory (ending with a trailing slash) with a ~ in the subtitle (windows)`() {
    val userHomeOnWindows = """C:\Users\goushik\"""
    val path = """${userHomeOnWindows}PyCharmProjects\django\.git"""

    assertThat(RecentGitRepository(path).subtitle(userHomeOnWindows))
      .isEqualTo("""~\PyCharmProjects\django""")
  }

  @Test
  @DisabledOnOs(OS.WINDOWS)
  fun `it should use the name of the project directory if the path is pointing to a git directory`() {
    val path = "/django/.git"
    assertThat(RecentGitRepository(path).title())
      .isEqualTo("django")
  }

  @Test
  @EnabledOnOs(OS.WINDOWS)
  fun `it should use the name of the project directory if the path is pointing to a git directory (windows)`() {
    val path = """C:\django\.git"""
    assertThat(RecentGitRepository(path).title())
      .isEqualTo("django")
  }

  @Test
  @DisabledOnOs(OS.WINDOWS)
  fun `it should drop the git directory when displaying a path to the project`() {
    val path = "/django/.git"
    assertThat(RecentGitRepository(path).subtitle(userHome))
      .isEqualTo("/django")
  }

  @Test
  @EnabledOnOs(OS.WINDOWS)
  fun `it should drop the git directory when displaying a path to the project (windows)`() {
    val path = """C:\django\.git"""
    assertThat(RecentGitRepository(path).subtitle(userHomeOnWindows))
      .isEqualTo("""C:\django""")
  }

  @ParameterizedTest
  @ValueSource(
    strings = [
      "",
      "/Users/varsha/mobius-demo",
      "/Users/varsha/hello-world/git",
      "/Users/varsha/todo/.gitignore",
      "/Users/varsha/focus/.gitignore",
      "/Users/varsha/zoom/.gi",
    ]
  )
  fun `it should not allow directory paths that don't point to a 'dot git' directory`(
    invalidDirectoryPath: String
  ) {
    val exception = assertThrows<IllegalArgumentException> {
      RecentGitRepository(invalidDirectoryPath)
    }
    assertThat(exception.message)
      .isEqualTo("Path should end with a '.git' directory, but was: $invalidDirectoryPath")
  }

  @Test
  @DisabledOnOs(OS.WINDOWS)
  fun `it should allow dot git directories with a trailing slash`() {
    val path = "$userHome/PyCharmProjects/django/.git/"

    assertThat(RecentGitRepository(path).title())
      .isEqualTo("django")
    assertThat(RecentGitRepository(path).subtitle(userHome))
      .isEqualTo("~/PyCharmProjects/django")
  }

  @Test
  @EnabledOnOs(OS.WINDOWS)
  fun `it should allow dot git directories with a trailing slash (windows)`() {
    val path = """$userHomeOnWindows\PyCharmProjects\django\.git\"""

    assertThat(RecentGitRepository(path).title())
      .isEqualTo("django")
    assertThat(RecentGitRepository(path).subtitle(userHomeOnWindows))
      .isEqualTo("""~\PyCharmProjects\django""")
  }

  @Test
  @DisabledOnOs(OS.WINDOWS)
  fun `it should get title and subtitle for a root git repository`() {
    val path = "/retrofit/.git/"

    assertThat(RecentGitRepository(path).title())
      .isEqualTo("retrofit")
    assertThat(RecentGitRepository(path).subtitle(userHome))
      .isEqualTo("/retrofit")
  }

  @Test
  @EnabledOnOs(OS.WINDOWS)
  fun `it should get title and subtitle for a root git repository (windows)`() {
    val path = """C:\retrofit\.git\"""

    assertThat(RecentGitRepository(path).title())
      .isEqualTo("retrofit")
    assertThat(RecentGitRepository(path).subtitle(userHomeOnWindows))
      .isEqualTo("""C:\retrofit""")
  }
}
