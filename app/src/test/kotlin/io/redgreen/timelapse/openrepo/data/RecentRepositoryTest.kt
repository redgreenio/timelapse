package io.redgreen.timelapse.openrepo.data

import com.google.common.truth.Truth.assertThat
import java.io.File
import org.junit.jupiter.api.Test

class RecentRepositoryTest {
  private val userHome = "/Users/goushik"

  @Test
  fun `it should use the name of the leaf directory as title`() {
    val path = "/Users/goushik/PyCharmProjects/django".replace('/', File.separatorChar)
    assertThat(RecentRepository(path).title)
      .isEqualTo("django")
  }

  @Test
  fun `it should return the exact path of directory if it is not present in the user's home directory`() {
    val path = "/PyCharmProjects/django".replace('/', File.separatorChar)

    assertThat(RecentRepository(path).subtitle(userHome))
      .isEqualTo(path)
  }

  @Test
  fun `it should replace the user's home directory with a ~ in the subtitle`() {
    val path = "$userHome/PyCharmProjects/django".replace('/', File.separatorChar)
    assertThat(RecentRepository(path).subtitle(userHome))
      .isEqualTo("~/PyCharmProjects/django")
  }

  @Test
  fun `it should replace the user's home directory (ending with a trailing slash) with a ~ in the subtitle`() {
    val userHome = "/Users/goushik/"
    val path = "${userHome}PyCharmProjects/django".replace('/', File.separatorChar)

    assertThat(RecentRepository(path).subtitle(userHome))
      .isEqualTo("~/PyCharmProjects/django")
  }
}
